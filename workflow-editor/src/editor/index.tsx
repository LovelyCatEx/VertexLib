import {createRoot} from "react-dom/client";
import {ClassicPreset, NodeEditor} from "rete";
import {AreaExtensions, AreaPlugin} from "rete-area-plugin";
import {ClassicFlow, ConnectionPlugin, getSourceTarget,} from "rete-connection-plugin";
import {Presets, type ReactArea2D, ReactPlugin, type RenderEmit} from "rete-react-plugin";
import type {ReteGraphSchemes} from "./types/editor-scheme.ts";
import {getConnectionSockets} from "./utils/socket-utils.ts";
import {ParameterSocket, parameterSocket, triggerSocket, TriggerSocket} from "./socket-definitions.ts";
import {TriggerConnectionComponent} from "./ui/connection/TriggerConnection.tsx";
import {ParameterConnectionComponent} from "./ui/connection/ParameterConnection.tsx";
import {ReteGraphNodeConnection} from "./types/connections.ts";
import {TriggerSocketComponent} from "./ui/socket/TriggerSocket.tsx";
import {ParameterSocketComponent} from "./ui/socket/ParameterSocket.tsx";
import {WorkFlowGraphNodeComponent} from "./ui/node/WorkFlowGraphNode.tsx";
import {applyCustomAreaBackground} from "./ui/background.ts";
import {type ContextMenuExtra, ContextMenuPlugin, Presets as ContextMenuPresets} from "rete-context-menu-plugin";
import {WORKFLOW_GRAPH_EDITOR_CONTEXT_MENUS} from "@/editor/context-menus.ts";
import {ContextMenuContainer} from "@/editor/ui/menu/ContextMenuContainer.tsx";
import {ContextMenuItem} from "@/editor/ui/menu/ContextMenuItem.tsx";
import {ContextMenuSubItem} from "@/editor/ui/menu/ContextMenuSubItem.tsx";
import {SquareFunction} from "lucide-react";

type AreaExtra = ReactArea2D<ReteGraphSchemes> | ContextMenuExtra;

export interface WorkFlowGraphEditorContext {
  editor: NodeEditor<ReteGraphSchemes>;
  area: AreaPlugin<ReteGraphSchemes, AreaExtra>;
  sockets: {
    trigger: ClassicPreset.Socket;
    data: ClassicPreset.Socket;
  };
  destroy(): void;
}

export interface CreateWorkFlowGraphEditorProps {
  onInvalidConnection?: () => void;
}

export async function createWorkFlowGraphEditor(
  container: HTMLElement,
  props?: CreateWorkFlowGraphEditorProps
): Promise<WorkFlowGraphEditorContext> {
  const editor = new NodeEditor<ReteGraphSchemes>();
  const area = new AreaPlugin<ReteGraphSchemes, AreaExtra>(container);
  const connection = new ConnectionPlugin<ReteGraphSchemes, AreaExtra>();
  const render = new ReactPlugin<ReteGraphSchemes, AreaExtra>({ createRoot });

  // Context Menu
  const contextMenu = new ContextMenuPlugin<ReteGraphSchemes>({
    items: ContextMenuPresets.classic.setup(WORKFLOW_GRAPH_EDITOR_CONTEXT_MENUS)
  });

  area.use(contextMenu);

  // Selectable Nodes
  AreaExtensions.selectableNodes(area, AreaExtensions.selector(), {
    accumulating: AreaExtensions.accumulateOnCtrl(),
  });


  render.addPreset(Presets.contextMenu.setup({
    customize: {
      main: () => {
        return ContextMenuContainer()
      },
      item: (item) => {
        return ContextMenuItem(item)
      },
      subitems: () => {
        return ContextMenuSubItem()
      },
      common: () => {
        // (props: React.HTMLProps<HTMLDivElement>) => {}
        return () => {
          return <div className="p-2 flex flex-row items-center space-x-2">
            <SquareFunction size="20" />
            <p>Create Node</p>
          </div>
        };
      }
    },
    delay: 500
  }));

  // render.addPreset(Presets.classic.setup());
  render.addPreset(
    Presets.classic.setup({
      customize: {
        connection(data) {
          const { source, target } = getConnectionSockets(editor, data.payload);

          if (
            source instanceof TriggerSocket ||
            target instanceof TriggerSocket
          ) {
            return TriggerConnectionComponent;
          }

          return ParameterConnectionComponent;
        },
        socket(data) {
          if (data.payload instanceof TriggerSocket) {
            return TriggerSocketComponent;
          }
          if (data.payload instanceof ParameterSocket) {
            return ParameterSocketComponent;
          }
          return Presets.classic.Socket;
        },
        node({ payload }) {
          return (data: { emit: RenderEmit<ReteGraphSchemes> }) => {
            return <WorkFlowGraphNodeComponent data={payload} emit={data.emit}/>
          };
        }
      },
    })
  );

  // connection.addPreset(ConnectionPresets.classic.setup());
  connection.addPreset(() => {
    return new ClassicFlow({
      canMakeConnection(from, to) {
        // this function checks if the old connection should be removed
        const [source, target] = getSourceTarget(from, to) || [null, null];

        if (!source || !target || from === to) return false;

        const sourceNode = editor.getNode(source.nodeId)!;
        const targetNode = editor.getNode(target.nodeId)!;

        const sockets = getConnectionSockets(
          editor,
          new ReteGraphNodeConnection(
            sourceNode,
            source.key as never,
            targetNode,
            target.key as never
          )
        );

        if (!sockets.source!.isCompatibleWith(sockets.target!)) {
          props?.onInvalidConnection?.();
          connection.drop();
          return false;
        }

        const connected = editor
          .getConnections()
          .find((conn) => conn.source == sourceNode.id &&
            conn.target == targetNode.id &&
            conn.sourceOutput == source.key &&
            conn.targetInput == target.key
          ) != null;

        // Already connected before
        if (connected) {
          connection.drop();
          return false;
        }

        return Boolean(source && target);
      },
      makeConnection(from, to, context) {
        const [source, target] = getSourceTarget(from, to) || [null, null];
        const { editor } = context;

        if (source && target) {
          void editor.addConnection(
            new ReteGraphNodeConnection(
              editor.getNode(source.nodeId)!,
              source.key as never,
              editor.getNode(target.nodeId)!,
              target.key as never
            )
          );
          return true;
        }
      },
    })
  })

  applyCustomAreaBackground(area);

  editor.use(area);
  area.use(connection);
  area.use(render);

  AreaExtensions.simpleNodesOrder(area);

  setTimeout(() => {
    // wait until nodes rendered because they dont have predefined width and height
    AreaExtensions.zoomAt(area, editor.getNodes());
  }, 10);

  return {
    editor,
    area,
    sockets: {
      trigger: triggerSocket,
      data: parameterSocket
    },
    destroy: () => area.destroy(),
  };
}
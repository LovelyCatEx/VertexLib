import {ClassicPreset, NodeEditor} from "rete";
import {ParameterSocket, type TriggerSocket} from "../socket-definitions.ts";
import type {ReteGraphSchemes} from "../types/editor-scheme.ts";

type SupportSockets = TriggerSocket | ParameterSocket;

type Input = ClassicPreset.Input<SupportSockets>;
type Output = ClassicPreset.Output<SupportSockets>;

/**
 * Find out the SocketType of source and target
 *
 * @param editor NodeEditor
 * @param connection
 */
export function getConnectionSockets(
  editor: NodeEditor<ReteGraphSchemes>,
  connection: ReteGraphSchemes["Connection"]
) {
  const source = editor.getNode(connection.source);
  const target = editor.getNode(connection.target);

  const output =
    source &&
    (source.outputs as Record<string, Output>)[connection.sourceOutput];
  const input =
    target && (target.inputs as Record<string, Input>)[connection.targetInput];

  return {
    source: output?.socket,
    target: input?.socket
  };
}

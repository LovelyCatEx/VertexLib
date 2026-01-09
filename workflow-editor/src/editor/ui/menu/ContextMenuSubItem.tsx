import * as React from "react";
import {forwardRef} from "react";
import {ContextMenuContainer} from "@/editor/ui/menu/ContextMenuContainer.tsx";

const ContextMenuContainerComponent = ContextMenuContainer()

export const ContextMenuSubItem = () => {
  return forwardRef<
    HTMLDivElement,
    React.HTMLProps<HTMLDivElement>
  >((props, ref) => {
    return <ContextMenuContainerComponent ref={ref}>
      {props.children}
    </ContextMenuContainerComponent>;
  });
}
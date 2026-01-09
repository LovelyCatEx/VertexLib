import {forwardRef} from "react";
import * as React from "react";

export const ContextMenuContainer = () => {
  return forwardRef<
    HTMLDivElement,
    React.HTMLProps<HTMLDivElement>
  >((props, ref) => {
    return <div
      ref={ref}
      className={props.className + " p-2 bg-white rounded-[.5rem] overflow-hidden shadow-2xl min-w-[256px]"}
      {...props}
    >
      {props.children}
    </div>;
  });
}
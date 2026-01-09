import * as React from "react";
import {forwardRef} from "react";
import type {Item} from "rete-react-plugin/_types/presets/context-menu/types";
import {ChevronRight} from "lucide-react";

export const ContextMenuItem = (item: Item) => {
  return forwardRef<
    HTMLDivElement,
    React.HTMLProps<HTMLDivElement>
  >((props, ref) => {
    const filteredChildren = React.Children.toArray(props.children).filter(
      (child) => React.isValidElement(child)
    );

    const normalDiv = <div
      ref={ref}
      className={props.className + " flex flex-row justify-between items-center " +
        "rounded-[.25rem] pt-2 pb-2 pl-4 pr-4 " +
        "bg-white text-black group " +
        "hover:bg-blue-500 hover:text-white transition cursor-pointer"
      }
      {...props}
    >
      <span>{item.label}</span>
      {(item.subitems?.length || 0) > 0 && <ChevronRight size="20" />}
      {(item.subitems?.length || 0) > 0 && (
        <div className="absolute left-full hidden group-hover:block hover:block z-50 ml-[-.5rem]">
          {filteredChildren}
        </div>
      )}
    </div>;

    const deleteDiv = <div
      ref={ref}
      className={props.className + " flex flex-row justify-between items-center " +
        "rounded-[.25rem] pt-2 pb-2 pl-4 pr-4 " +
        "bg-white text-black group " +
        "hover:bg-red-400 hover:text-white transition cursor-pointer"
      }
      {...props}
    >
      <p>{item.label}</p>
    </div>

    return item.key == 'delete' ? deleteDiv : normalDiv;
  });
}
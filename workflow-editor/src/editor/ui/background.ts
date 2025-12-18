import type {BaseSchemes} from "rete";
import { AreaPlugin } from "rete-area-plugin";
import './background.css';

export function applyCustomAreaBackground<S extends BaseSchemes, K>(
  area: AreaPlugin<S, K>
) {
  const background = document.createElement("div");

  background.classList.add("area-background");
  background.classList.add("area-fill");

  area.area.content.add(background);
}

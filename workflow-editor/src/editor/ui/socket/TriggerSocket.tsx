import {ClassicPreset} from "rete";
import styled from "styled-components";
import {socketMarginHorizontal, socketMarginVertical, socketSize} from "../graph-ui-constants";

const Svg = styled.svg`
  stroke: white;
  fill: #ffffff47;
  stroke-width: 2px;
  stroke-linejoin: round;
  :hover {
    stroke-width: 5px;
  }
`

export function TriggerSocketComponent<T extends ClassicPreset.Socket>(props: {
  data: T;
}) {
  return <div
    style={{margin: `${socketMarginVertical} ${socketMarginHorizontal}`}}
    title={props.data.name}
  >
    <Svg xmlns="http://www.w3.org/2000/svg" viewBox="-2 -2 25 24" width={socketSize} height={socketSize}>
      <path d="M0,0 L10,0 L20,10 L10,20 L0,20 Z" />
    </Svg>
  </div>;
}
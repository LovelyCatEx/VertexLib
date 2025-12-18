import { ClassicPreset } from "rete";
import styled from "styled-components";
import {socketMarginHorizontal, socketMarginVertical, socketSize} from "../graph-ui-constants";

const ParameterSocket = styled.div`
  position: relative;
  display: block;
  cursor: pointer;

  width: calc(${socketSize} * 1.6);
  height: ${socketSize};

  margin: ${socketMarginVertical} ${socketMarginHorizontal};
  z-index: 2;
    
  &::before {
    content: "";
    position: absolute;
    left: 0;
    top: 50%;

    width: ${socketSize};
    height: ${socketSize};

    transform: translateY(-50%);
    border-radius: 50%;

    background: linear-gradient(
      180deg,
      #ff8a8a 0%,
      #d94a4a 100%
    );

    border: 2px solid #b30000b8;

    box-shadow:
      inset 0 0 1px rgba(0, 0, 0, 0.4),
      0 1px 2px rgba(0, 0, 0, 0.6);

    box-sizing: border-box;
    transition: filter 0.12s ease;
  }

  &::after {
    content: "";
    position: absolute;
    left: calc(${socketSize} * 0.9);
    top: 50%;

    transform: translateX(2px) translateY(-50%);

    width: 0;
    height: 0;

    border-top: calc(${socketSize} * 0.25) solid transparent;
    border-bottom: calc(${socketSize} * 0.25) solid transparent;
    border-left: calc(${socketSize} * 0.5) solid #b30000b8;
  }

  &:hover::before {
    filter: brightness(1.15);
  }

  &.multiple::before {
    background: linear-gradient(
      180deg,
      #fff59d 0%,
      #fbc02d 100%
    );
    border-color: #f9a825;
  }
`;

export function ParameterSocketComponent<T extends ClassicPreset.Socket>(props: {
  data: T;
}) {
  return <ParameterSocket title={props.data.name} />;
}

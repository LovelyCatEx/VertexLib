import {css} from "styled-components";
import {type ClassicScheme, Presets} from "rete-react-plugin";

const { Connection } = Presets.classic;

const styles = css`
    stroke: rgba(255, 255, 255, 0.8);
    stroke-dasharray: 10 5;
    animation: dash 1s linear infinite;
    stroke-dashoffset: 45;
    @keyframes dash {
        to {
            stroke-dashoffset: 0;
        }
    }
`;

export function TriggerConnectionComponent(props: {
  data: ClassicScheme["Connection"] & { isLoop?: boolean };
}) {
  return <Connection {...props} styles={() => styles} />;
}

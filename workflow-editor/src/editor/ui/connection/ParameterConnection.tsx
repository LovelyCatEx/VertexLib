import {css} from "styled-components";
import {type ClassicScheme, Presets} from "rete-react-plugin";

const { Connection } = Presets.classic;

const styles = css`
    stroke: #b30000b8;
`;

export function ParameterConnectionComponent(props: {
  data: ClassicScheme["Connection"] & { isLoop?: boolean };
}) {
  return <Connection {...props} styles={() => styles} />;
}
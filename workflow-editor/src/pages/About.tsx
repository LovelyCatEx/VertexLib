import {useRete} from "rete-react-plugin";
import {createWorkFlowGraphEditor} from "../editor/workflow-editor.tsx";

export default function About() {
  const [ref] = useRete(createWorkFlowGraphEditor);

  return (
    <div className="App">
      <div ref={ref} style={{ height: "100vh", width: "100vw" }}></div>
    </div>
  )
}

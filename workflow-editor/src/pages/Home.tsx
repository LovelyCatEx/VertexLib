import '../App.css'
import {WorkFlowGraphEditor} from "../editor/WorkFlowGraphEditor.tsx";
import {MOCK_GRAPH_JSON} from "../types/workflow-graph.ts";

export default function Home() {
  return (
    <WorkFlowGraphEditor className="w-full h-[100vh]" graphData={MOCK_GRAPH_JSON} />
  );
}
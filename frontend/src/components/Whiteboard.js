"use client";


import { useEffect, useRef, useState } from "react";
export default function Whiteboard() {
const canvasRef = useRef(null);
const [ws, setWs] = useState(null);
const [isDrawing, setIsDrawing] = useState(false);
const [points, setPoints] = useState([]);


useEffect(() => {
const socket = new WebSocket("ws://localhost:8080/ws/whiteboard");
socket.onopen = () => console.log("Connected to backend WS");
socket.onmessage = (msg) => handleIncoming(JSON.parse(msg.data));
setWs(socket);
return () => socket.close();
}, []);


const handleIncoming = (payload) => {
const ctx = canvasRef.current.getContext("2d");
if (Array.isArray(payload)) {
ctx.clearRect(0, 0, 2000, 2000);
payload.forEach(drawStroke);
} else if (payload.type === "add") {
drawStroke(payload.stroke);
} else if (payload.type === "remove") {
// no-op for now
} else if (payload.type === "clear") {
ctx.clearRect(0, 0, 2000, 2000);
}
};
const drawStroke = (stroke) => {
const y = e.clientY - rect.top;
setPoints([{ x, y }]);
};


const handleMouseMove = (e) => {
if (!isDrawing) return;
const rect = canvasRef.current.getBoundingClientRect();
const x = e.clientX - rect.left;
const y = e.clientY - rect.top;
setPoints((prev) => [...prev, { x, y }]);


const ctx = canvasRef.current.getContext("2d");
ctx.lineWidth = 2;
ctx.strokeStyle = "black";
ctx.beginPath();


const pts = [...points, { x, y }];
ctx.moveTo(pts[0].x, pts[0].y);
pts.forEach((p) => ctx.lineTo(p.x, p.y));
ctx.stroke();
};


const handleMouseUp = () => {
setIsDrawing(false);
if (ws && points.length > 1) {
const msg = {
type: "add",
stroke: {
points,
color: "black",
thickness: 2,
timestamp: Date.now(),
author: "client",
},
};
ws.send(JSON.stringify(msg));
}
setPoints([]);
};


const clearBoard = () => {
if (ws) ws.send(JSON.stringify({ type: "clear" }));
};


return (
<div style={{ position: "relative", width: "100%", height: "100%" }}>
<button
onClick={clearBoard}
style={{ position: "absolute", top: 20, left: 20, zIndex: 10 }}
>
Clear
</button>


<canvas
ref={canvasRef}
width={2000}
height={2000}
style={{ width: "100%", height: "100%", cursor: "crosshair" }}
onMouseDown={handleMouseDown}
onMouseMove={handleMouseMove}
onMouseUp={handleMouseUp}
onMouseLeave={handleMouseUp}
/>
</div>
);
}
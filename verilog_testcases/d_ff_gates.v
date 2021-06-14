module D_FlipFlop_Gates (D, clk, Q);

input D;
input clk;
output Q;

wire y1;
wire y2;
wire y3;
wire y4;

assign y1 = D ~| clk;
assign y2 = y1 ~| clk;
assign y3 = y1 ~| y4;
assign y4 = y2 ~| y3;

assign Q = y3;
endmodule

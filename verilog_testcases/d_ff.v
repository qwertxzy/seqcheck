module RisingEdge_D_FF_AsyncResetLow (D, clk, async_reset, Q);

input D; // data input
input clk; // clock input
input async_reset; // async reset signal, active low
output reg Q;

always @(posedge clk or negedge async_reset)
begin
	if (async_reset == 1'b0)
		Q <= 1'b0;
	else
		Q <= D;
end
endmodule
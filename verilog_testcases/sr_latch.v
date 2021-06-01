module SR_Latch (S, R, Q);

input S; // set input
input R; // reset input
output reg Q; // output signal

always @(*) begin
  if (S == 1)
    Q <= 1;
  else if (R == 1)
    Q <= 0;
end
endmodule
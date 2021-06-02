module FourBit_Counter (
  input clk, // clock input
  input inc, // increment signal
  input async_reset, // reset signal

  output [3:0] count // output of the count signal
);

reg [3:0] cnt_internal;

  always @(posedge clk or negedge async_reset)
  begin
    if (async_reset == 1)
      cnt_internal <= 0;
    else
      cnt_internal <= cnt_internal + 1;
  end

assign count = cnt_internal;
endmodule
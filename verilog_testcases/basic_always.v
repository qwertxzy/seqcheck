module always_star(input a, b, c, output reg x);
  always @*
  begin
    x = (a & b) | c;
  end
endmodule
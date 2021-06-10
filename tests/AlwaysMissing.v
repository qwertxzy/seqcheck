module always_missing(input a, b, c, output reg x);
  always @(a, b)
  begin
    x <= (a & b) | c;
  end
endmodule
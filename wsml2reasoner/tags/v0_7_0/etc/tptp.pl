#!/usr/bin/perl

if ($ENV{'REQUEST_METHOD'} eq 'POST') {
  # Get the input

  read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});

  # Split the name-value pairs
  #
  @pairs = split(/&/, $buffer);
  foreach $pair (@pairs) {
    ($name, $value) = split(/=/, $pair);
    $value =~ tr/+/ /;
    $value =~ s/%([a-fA-F0-9][a-fA-F0-9])/pack("C", hex($1))/eg;
    $FORM{$name} = $value;
  }
}
print "Content-Type: text/plain\n\n";

open(DAT,">/tmp/somefile.tptp");
print DAT "$FORM{theory}"; 
close(DAT);
system("./tptp.sh");


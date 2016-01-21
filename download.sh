#!/usr/bin/expect -f
spawn java -jar EmailExport.jar
expect "Your selection:"
send "2\r"
expect "Please enter your username:"
send "MYEMAILADDRESS\r"
expect "Please enter your password:"
send "MYPASSWORD\r"
expect "Your selection:"
send "1\r"
expect "Please enter first date (yyyy/MM/dd) to download:"
send "2008/01/01\r"
expect "Please enter last date (yyyy/MM/dd) to download:"
send "2017/01/01\r"
expect "Your selection:"
send "2\r"
expect "Your selection:"
send "2\r"
set timeout -1
expect eof


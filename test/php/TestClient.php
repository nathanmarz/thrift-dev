<?php

/** Include the Thrift base */
require_once '/home/mcslee/code/projects/thrift/lib/php/src/Thrift.php';

/** Include the binary protocol */
require_once THRIFT_ROOT.'/protocol/TBinaryProtocol.php';

/** Include the socket layer */
require_once THRIFT_ROOT.'/transport/TSocket.php';

/** Include the socket layer */
require_once THRIFT_ROOT.'/transport/TBufferedTransport.php';

/** Include the generated code */
require_once '/home/mcslee/code/projects/thrift/test/php/gen-php/ThriftTest.php';

$host = 'localhost';
$port = 9090;

if ($argc > 1) {
  $host = $argv[0];
}

if ($argc > 2) {
  $host = $argv[1];
}

$socket = new TSocket($host, $port);
$bufferedSocket = new TBufferedTransport($socket, 1024, 1024);
$binary = new TBinaryProtocol();

$testClient = new ThriftTestClient($bufferedSocket, $binary);
$bufferedSocket->open();

$start = microtime(true);

/**
 * VOID TEST
 */
print_r("testVoid()");
$testClient->testVoid();
print_r(" = void\n");

/**
 * STRING TEST
 */
print_r("testString(\"Test\")");
$s = $testClient->testString("Test");
print_r(" = \"$s\"\n");
   
/**
 * BYTE TEST
 */
print_r("testByte(1)");
$u8 = $testClient->testByte(1);
print_r(" = $u8\n");

/**
 * I32 TEST
 */
print_r("testI32(-1)");
$i32 = $testClient->testI32(-1);
print_r(" = $i32\n");

/**
 * I64 TEST
 */
print_r("testI64(-34359738368)");
$i64 = $testClient->testI64(-34359738368);
print_r(" = $i64\n");

/**
 * STRUCT TEST
 */
print_r("testStruct({\"Zero\", 1, -3, -5})");
$out = new Xtruct();
$out->string_thing = "Zero";
$out->byte_thing = 1;
$out->i32_thing = -3;
$out->i64_thing = -5;
$in = $testClient->testStruct($out);
print_r(" = {\"".$in->string_thing."\", ".
        $in->byte_thing.", ".
        $in->i32_thing.", ".
        $in->i64_thing."}\n");

/**
 * NESTED STRUCT TEST
 */
print_r("testNest({1, {\"Zero\", 1, -3, -5}), 5}");
$out2 = new Xtruct2();
$out2->byte_thing = 1;
$out2->struct_thing = $out;
$out2->i32_thing = 5;
$in2 = $testClient->testNest($out2);
$in = $in2->struct_thing;
print_r(" = {".$in2->byte_thing.", {\"".
        $in->string_thing."\", ".
        $in->byte_thing.", ".
        $in->i32_thing.", ".
        $in->i64_thing."}, ".
        $in2->i32_thing."}\n");

/**
 * MAP TEST
 */
$mapout = array();
for ($i = 0; $i < 5; ++$i) {
  $mapout[$i] = $i-10;
}
print_r("testMap({");
$first = true;
foreach ($mapout as $key => $val) {
  if ($first) {
    $first = false;
  } else {
    print_r(", ");
  }
  print_r("$key => $val");
}
print_r("})");

$mapin = $testClient->testMap($mapout);
print_r(" = {");
$first = true;
foreach ($mapin as $key => $val) {
  if ($first) {
    $first = false;
  } else {
    print_r(", ");
  }
  print_r("$key => $val");
}
print_r("}\n");

/**
 * SET TEST
 */
$setout = array();;
for ($i = -2; $i < 3; ++$i) {
  $setout []= $i;
}
print_r("testSet({");
$first = true;
foreach ($setout as $val) {
  if ($first) {
    $first = false;
  } else {
    print_r(", ");
  }
  print_r($val);
}
print_r("})");
$setin = $testClient->testSet($setout);
print_r(" = {");
$first = true;
foreach ($setin as $val) {
  if ($first) {
    $first = false;
  } else {
    print_r(", ");
  }
  print_r($val);
}
print_r("}\n");

/**
 * LIST TEST
 */
$listout = array();
for ($i = -2; $i < 3; ++$i) {
  $listout []= $i;
}
print_r("testList({");
$first = true;
foreach ($listout as $val) {
  if ($first) {
    $first = false;
  } else {
    print_r(", ");
  }
  print_r($val);
}
print_r("})");
$listin = $testClient->testList($listout);
print_r(" = {");
$first = true;
foreach ($listin as $val) {
  if ($first) {
    $first = false;
  } else {
    print_r(", ");
  }
  print_r($val);
}
print_r("}\n");

/**
 * ENUM TEST
 */
print_r("testEnum(ONE)");
$ret = $testClient->testEnum(Numberz::ONE);
print_r(" = $ret\n");

print_r("testEnum(TWO)");
$ret = $testClient->testEnum(Numberz::TWO);
print_r(" = $ret\n");

print_r("testEnum(THREE)");
$ret = $testClient->testEnum(Numberz::THREE);
print_r(" = $ret\n");

print_r("testEnum(FIVE)");
$ret = $testClient->testEnum(Numberz::FIVE);
print_r(" = $ret\n");

print_r("testEnum(EIGHT)");
$ret = $testClient->testEnum(Numberz::EIGHT);
print_r(" = $ret\n");

/**
 * TYPEDEF TEST
 */
print_r("testTypedef(309858235082523)");
$uid = $testClient->testTypedef(309858235082523);
print_r(" = $uid\n");

/**
 * NESTED MAP TEST
 */
print_r("testMapMap(1)");
$mm = $testClient->testMapMap(1);
print_r(" = {");
foreach ($mm as $key => $val) {
  print_r("$key => {");
  foreach ($val as $k2 => $v2) {
    print_r("$k2 => $v2, ");
  }
  print_r("}, ");
}
print_r("}\n");

/**
 * INSANITY TEST
 */
$insane = new Insanity();
$insane->userMap[Numberz::FIVE] = 5000;
$truck = new Xtruct();
$truck->string_thing = "Truck";
$truck->byte_thing = 8;
$truck->i32_thing = 8;
$truck->i64_thing = 8;
$insane->xtructs []= $truck;
print_r("testInsanity()");
$whoa = $testClient->testInsanity($insane);
print_r(" = {");
foreach ($whoa as $key => $val) {
  print_r("$key => {");
  foreach ($val as $k2 => $v2) {
    print_r("$k2 => {");
    $userMap = $v2->userMap;
    print_r("{");
    foreach ($userMap as $k3 => $v3) {
      print_r("$k3 => $v3, ");
    }
    print_r("}, ");
    
    $xtructs = $v2->xtructs;
    print_r("{");
    foreach ($xtructs as $x) {
      print_r("{\"".$x->string_thing."\", ".
              $x->byte_thing.", ".$x->i32_thing.", ".$x->i64_thing."}, ");
    }
    print_r("}");
    
    print_r("}, ");
  }
  print_r("}, ");
}
print_r("}\n");


/**
 * Normal tests done.
 */

$stop = microtime(true);
$elp = round(1000*($stop - $start), 0);
print_r("Total time: $elp ms\n");

/**
 * Extraneous "I don't trust PHP to pack/unpack integer" tests
 */

// Max I32
$num = pow(2, 30) + (pow(2, 30) - 1);
$num2 = $testClient->testI32($num);
if ($num != $num2) {
  print "Missed $num = $num2\n";
}

// Min I32
$num = 0 - pow(2, 31);
$num2 = $testClient->testI32($num);
if ($num != $num2) {
  print "Missed $num = $num2\n";
}

// Max I64
$num = pow(2, 62) + (pow(2, 62) - 1);
$num2 = $testClient->testI64($num);
if ($num != $num2) {
  print "Missed $num = $num2\n";
}

// Min I64
$num = 0 - pow(2, 63);
$num2 = $testClient->testI64($num);
if ($num != $num2) {
  print "Missed $num = $num2\n";
}

$bufferedSocket->close();
return;

?>

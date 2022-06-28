<?php
session_id("session3");
session_start();
$username = $_SESSION['username'];
?>
<html>
<head>
    <title>My Messages</title>
    <link rel="icon" href="https://www.flaticon.com/svg/static/icons/svg/1411/1411824.svg">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.css">
    <link rel="stylesheet" href="messagestyles.css">
    <style type="text/css">
        body{ font: 14px sans-serif; }
        .wrapper{ width: 350px; padding: 20px; }
    </style>
</head>

<body style="background-color: #90ccf4">

<div class="wraps">
    <div class="multi_color_border"></div>
    <div class="top_nav">
        <div class="left">
            <div class="logo"><p style="font-size: 24pt"><span>Simple Telegraph System</span></p></div>
           </div>
        <div class="right" id="submit"> <li><a href="messagelogin.php" style="color: #ffffff"><?php print($username);?></a> </li></div>
        <style type="text/css">
            #submit {
                text-transform: uppercase;
                letter-spacing: 3px;
                text-space: ;
                background-color: #337ab7;
                border: none;
                color: white;
                padding: 10px 15px;
                text-align: center;
                text-decoration: none;
                display: inline-block;
                font-size: 16px;
                margin: 1px 2px;
                cursor: pointer;
            }
            #submit:hover {
                border: #000000;
                color: #ffffff;
                background: #225a80;
                box-shadow: 0px 0px 1px #777;
            }
        </style>
    </div>
    <div class="bottom_nav">
        <ul>
            <li><a href="messages.php">HOME</a></li>
            <li><a href="signout.php">SIGN OUT</a></li>
        </ul>
    </div>
</div>
<iframe src="messagesearch.php" onload='javascript:(function(o){o.style.height=o.contentWindow.document.body.scrollHeight+"px";}(this));' style="height:200px;width:100%;border:none;overflow:hidden;"></iframe>
<br><br>
<p style=" padding-left: 5mm; font-size: 22pt">My Messages</p>
<div style="padding-left: 5mm">
    <?php
    $dbhost = 'localhost:3306';
    $dbuser = 'root';
    $dbpass = '';

    $_PHP_SELF = $_SERVER['PHP_SELF'];

    $rec_limit = 5000;
    $conn = mysqli_connect($dbhost, $dbuser, $dbpass);

    if(! $conn ) {
        die('Could not connect: ' . mysqli_error());
    }
    else{print"Connected<br>";}
    mysqli_select_db($conn,'sakila');

    /* Get total number of records */
    $sql = "SELECT count(message) FROM telegraph ";
    $retval = mysqli_query( $conn, $sql);

    if(! $retval ) {
        die('Could not get data: ' . mysqli_error($conn));
    }
    $row = mysqli_fetch_array($retval, MYSQLI_NUM );
    $rec_count = $row[0];

    if( isset($_GET{'page'} ) ) {
        $page = $_GET{'page'} + 1;
        $offset = $rec_limit * $page ;
    }else {
        $page = 0;
        $offset = 0;
    }

    $left_rec = $rec_count - ($page * $rec_limit);
    $sql = "SELECT username, message, sender FROM telegraph WHERE username='$username' LIMIT $offset, $rec_limit";

    $retval = mysqli_query($conn, $sql);

    if(! $retval ) {
        die('Could not get data: ' . mysqli_error($conn));
    }
    if (mysqli_num_rows($retval) > 0) {
        echo($rec_count." Messages available.<br><br>");
        while($row = mysqli_fetch_array($retval, MYSQLI_ASSOC)) {
            echo
                "<b>Sender:</b> {$row['sender']} <br> ".
                "<b>Message:</b> {$row['message']} <br> ".
                "--------------------------------<br>";
        }
    } else {
        echo "No items available<br><br>";
    }

    if( $page > 0 ) {
        $last = $page - 2;
        echo "<a href = \"$_PHP_SELF?page = $last\">Last 10 Records</a> |";
        echo "<a href = \"$_PHP_SELF?page = $page\">Next 10 Records</a>";
    }else if( $page == 0 ) {
        echo "<a href = \"$_PHP_SELF?page = $page\">Next 20 Records</a>";
    }else if( $left_rec < $rec_limit ) {
        $last = $page - 2;
        echo "<a href = \"$_PHP_SELF?page = $last\">Last 10 Records</a>";
    }

    mysqli_close($conn);
    ?>
</div>
</body>
</html>
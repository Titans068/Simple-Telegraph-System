<?php
session_id("session3");
session_start();
$username = $_SESSION['username'];
?>
<html>

<head>
    <link rel="icon" href="https://www.flaticon.com/svg/static/icons/svg/2897/2897763.svg">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.css">
    <link rel="stylesheet" href="messagestyles.css">
    <title>Search Records</title>
    <script>
        function resizeIframe(obj) {
            obj.style.height = obj.contentWindow.document.documentElement.scrollHeight + 'px';
        }
    </script>
</head>

<body style="padding-left: 5mm;background-color: #90ccf4">
<p style="font-size: 22pt">Search My Messages</p>
<p style="font-size: 12pt">Enter search term</p>
<form method = "post" action = "messagesearch.php">
    <table width = "400" border = "0" cellspacing = "1"
           cellpadding = "2">

        <tr>
            <td><input class="form-control" name = "searches" type = "text"
                       id = "searches"></td>
            <td> <div style="padding-top: 4mm" class="form-group">
                    <input class="btn btn-primary" name = "searcher" type = "submit"
                           id = "searcher" value = "⌕" style="font-size: 18px">
                </div></td>
        </tr>

        <tr>
            <td>

            </td>
        </tr>

    </table>
    <br>

</form>
<script>
    var input = document.getElementById("searcher");
    // Execute a function when the user releases a key on the keyboard
    input.addEventListener("keyup", function(event) {
        // Number 13 is the "Enter" key on the keyboard
        if (event.keyCode === 13) {
            // Cancel the default action, if needed
            event.preventDefault();
            // Trigger the button element with a click
            document.getElementById("searcher").click();
        }
    });
</script>
<?php

if(isset($_POST['searcher'])) {
    $dbhost = 'localhost:3306';
    $dbuser = 'root';
    $dbpass = '';
    $database='sakila';

    $_PHP_SELF = $_SERVER['PHP_SELF'];

    $rec_limit = 10;
    $conn = mysqli_connect($dbhost, $dbuser, $dbpass,$database);

    if (!$conn) {
        die('Could not connect: ' . mysqli_error($conn));
    }
    $searches = $_POST['searches'];

    $query = "SELECT * FROM telegraph WHERE username='$username' && CONCAT (message,sender) like '%$searches%'";

    $result = mysqli_query($conn, $query);
    print_r(mysqli_num_rows($result)." <b>results matched.</b><br><br>");
    if ($result) {
        while ($row = mysqli_fetch_row($result)) {
            print
                "<b>Sender:</b> {$row[2]} <br> ".
                "<b>Message:</b> {$row[1]} <br> ".
                "--------------------------------<br>";
        }
    } else {
        print ('No result'.mysqli_error($conn));
    }
}else{}
?>

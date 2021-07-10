<?php 
    require_once 'ServerResponse.php';
    
    $part = $_GET["part"];
    $action = $_GET["action"];
    $detalAction = $_GET["detalAction"];
    $typeQuery = 0;


    if ($action == "insert" || $action == "update" ||
        $action == "delete" || $action == "clear"){
        $typeQuery = 0;
    } else {
        $typeQuery = 1;
    }

    switch ($part) {
        
        case 'users': 
                
                switch ($action) {
                    
                    case 'get':
                        
                        switch ($detalAction ) {
                            
                            case 'checkExistUser':
                                
                                if (isset($_GET["login"])){
                                    $login = $_GET["login"];
                                    $query = "SELECT id FROM users WHERE login = \"".$login."\"";
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse(); 
                                }
                            break;


                            case 'getID':
                                
                                if (isset($_GET["login"]) && isset($_GET["password"])){
                                    $login = $_GET["login"];
                                    $password = $_GET["password"];
                                    $query = "SELECT id FROM users WHERE login = \"".$login."\""." AND password = \"".$password."\"";
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse(); 
                                }
                            break;


                            case 'getAllUsers':
                                
                                $query = "SELECT login FROM users";
                                $server = new ServerResponse($query, $typeQuery);
                                echo $server->getResponse();
                            break;

                        }
                    break;
                    

                    case 'insert':

                         if (isset($_GET["login"]) && isset($_GET["password"])){
                            $login = $_GET["login"];
                            $password = $_GET["password"]; 
                            $query = "INSERT INTO users VALUES(NULL, '".$login."','".$password."')";
                            $server = new ServerResponse($query, $typeQuery);
                        }
                    break;
                    

                    case 'update':
                            
                            if (isset($_GET["login"]) && isset($_GET["password"])){
                                $login = $_GET["login"];
                                $password = $_GET["password"]; 
                                $query = "UPDATE users SET password=".$password." WHERE login=".$login;
                                $server = new ServerResponse($query, $typeQuery);
                            }
                    break;
                    

                    case 'delete':

                        $query = "DELETE FROM users WHERE id=2";
                        $server = new ServerResponse($query, $typeQuery);
                    break;
                    

                    case 'clear':

                        $query = "DELETE FROM users";
                        $server = new ServerResponse($query, $typeQuery);
                    break;
                } 
        break;
        









        case 'chanel': 
                
                switch ($action) {
                    
                    case 'get':
                        
                        switch ($detalAction) {
                            
                            case 'getMyChanel':
                                
                                if (isset($_GET["login"])) {
                                    $login = $_GET["login"];
                                    $query = "SELECT chanelName FROM chanel WHERE fromLogin = \"".$login."\" OR toLogin = \"".$login."\"";
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                }
                            break;


                            case 'getIDMyChanel':
                                // ""select id from chanel_n where (from_login={0} and to_login={1}) or (from_login={1} and to_login={0});".format(from_u,to_u)
                                if (isset($_GET["fromLogin"]) && isset($_GET["toLogin"])) {
                                        $fromLogin = $_GET["fromLogin"];
                                        $toLogin = $_GET["toLogin"];
                                        $query = "SELECT id FROM chanel WHERE (fromLogin=\"".$fromLogin."\" AND  toLogin=\"".$toLogin."\") OR (fromLogin=\"".$toLogin."\" AND toLogin = \"".$fromLogin."\")";
                                        $server = new ServerResponse($query, $typeQuery);
                                        echo $server->getResponse();                         
                                } 
                            break;
                        } 
                    break;
                    

                    case 'insert':

                        if (isset($_GET["chanelName"]) && isset($_GET["fromLogin"]) && isset($_GET["toLogin"])){
                            $chanelName = $_GET["chanelName"];
                            $fromLogin = $_GET["fromLogin"];
                            $toLogin = $_GET["toLogin"];
                            $query = "INSERT INTO chanel VALUES(NULL, '".$chanelName."','".$fromLogin."','".$toLogin."')";
                            $server = new ServerResponse($query, $typeQuery);

                        }
                    break;
                    

                    case 'update':
                            
                            // if (isset($_GET["login"]) && isset($_GET["password"])){
                            //     $login = $_GET["login"];
                            //     $password = $_GET["password"]; 
                            //     $query = "UPDATE chanel SET password=".$password." WHERE login=".$login;
                            //     $server = new ServerResponse($query, $typeQuery);
                            // }
                            
                    break;
                    

                    case 'delete':
                        
                        // $query = "DELETE FROM chanel WHERE id=2";
                        // $server = new ServerResponse($query, $typeQuery);
                    break;
                    

                    case 'clear':
                        $query = "DELETE FROM chanel";
                        $server = new ServerResponse($query, $typeQuery);
                    break;
                } 
        break;
        









        case 'messages': 
                
                switch ($action) {
                    
                    case 'get':
                        
                        switch ($detalAction) {
                            
                            case 'getConversation':
                                //"select mess from mess_n where (from_login={0} and to_login={1}) or (from_login={1} and to_login={0});".format(from_u, to_u)
                                if (isset($_GET["fromLogin"]) && isset($_GET["toLogin"])) {
                                    $fromLogin = $_GET["fromLogin"];
                                    $toLogin = $_GET["toLogin"];
                                    $query = "SELECT mess FROM messages WHERE (fromLogin=\"".$fromLogin."\" AND toLogin=\"".$toLogin."\") OR (fromLogin=\"".$toLogin."\" AND toLogin=\"".$fromLogin."\")";
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                } 
                            break;

                            case 'getCountConversation':
                                //"select mess from mess_n where (from_login={0} and to_login={1}) or (from_login={1} and to_login={0});".format(from_u, to_u)
                                if (isset($_GET["fromLogin"]) && isset($_GET["toLogin"])) {
                                    $fromLogin = $_GET["fromLogin"];
                                    $toLogin = $_GET["toLogin"];
                                    $query = "SELECT COUNT(mess) FROM messages WHERE (fromLogin=\"".$fromLogin."\" AND toLogin=\"".$toLogin."\") OR (fromLogin=\"".$toLogin."\" AND toLogin=\"".$fromLogin."\")";
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                } 
                            break;
                        } 
                    break;
                    

                    case 'insert':

                        if (isset($_GET["fromLogin"]) && isset($_GET["toLogin"]) && isset($_GET["mess"])) {
                            $fromLogin = $_GET["fromLogin"];
                            $toLogin = $_GET["toLogin"];
                            $mess = $_GET["mess"];
                            $query = "INSERT INTO messages VALUES(NULL, '".$fromLogin."','".$toLogin."','".$mess."')";
                            $server = new ServerResponse($query, $typeQuery);

                        }
                    break;
                    

                    case 'update':
                            
                            // if (isset($_GET["login"]) && isset($_GET["password"])){
                            //     $login = $_GET["login"];
                            //     $password = $_GET["password"]; 
                            //     $query = "UPDATE chanel SET password=".$password." WHERE login=".$login;
                            //     $server = new ServerResponse($query, $typeQuery);
                            // }
                            
                    break;
                    

                    case 'delete':
                        
                        // $query = "DELETE FROM chanel WHERE id=2";
                        // $server = new ServerResponse($query, $typeQuery);
                    break;

                    
                    case 'clear':
                        $query = "DELETE FROM messages";
                        $server = new ServerResponse($query, $typeQuery);
                    break;
                    
                } 
        break;










        case 'group_admin': 
                
                switch ($action) {
                    
                    case 'get':
                        
                        switch ($detalAction) {
                            
                            case 'getIDGroup':
                                //"select id from admin_group_n where name_group = {0};".format(name_group)
                                if (isset($_GET["nameGroup"])) {
                                    $nameGroup = $_GET["nameGroup"];
                                    $query = "SELECT id FROM group_admin WHERE nameGroup=\"".$nameGroup."\"";
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                } 
                            break;

                                
                            case 'getListGroups':
                                    // "select name_group from admin_group_n;"
                                    $query = "SELECT nameGroup FROM group_admin";
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                            break;

                               
                            case 'getPassGroup':
                                if (isset($_GET["nameGroup"])) {
                                    $nameGroup = $_GET["nameGroup"];
                                    $query = "SELECT pass FROM group_admin WHERE nameGroup=\"".$nameGroup."\"";
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                }
                            break;
                        } 
                    break;


                    case 'insert':
                        // "insert into admin_group_n (author, pass, name_group) values ({0},{1},{2});".format(author, password, name_group)
                        if (isset($_GET["author"]) && isset($_GET["pass"]) && isset($_GET["nameGroup"])) {
                            $author = $_GET["author"];
                            $pass = $_GET["pass"];
                            $nameGroup = $_GET["nameGroup"];
                            $query = "INSERT INTO group_admin VALUES(NULL, '".$author."','".$pass."','".$nameGroup."')";
                            $server = new ServerResponse($query, $typeQuery);
                        }
                    break;
                    

                    case 'update':
                            // if (isset($_GET["login"]) && isset($_GET["password"])){
                            //     $login = $_GET["login"];
                            //     $password = $_GET["password"]; 
                            
                            //     $query = "UPDATE chanel SET password=".$password." WHERE login=".$login;
                            //     $server = new ServerResponse($query, $typeQuery);
                            // }
                            
                    break;
                   

                    case 'delete':
                            // $query = "DELETE FROM chanel WHERE id=2";
                            // $server = new ServerResponse($query, $typeQuery);
                    break;

                    
                    case 'clear':
                        $query = "DELETE FROM group_admin";
                        $server = new ServerResponse($query, $typeQuery);
                    break;
                    
                } 
        break;










        case 'group_user': 
                
                switch ($action) {
                    
                    case 'get':
                            
                       switch ($detalAction) {
                            
                            case 'getListMyGroups':
                                // "select n_group from user_group_n where login = {0};".format(login)
                                if (isset($_GET["login"])) {
                                    $login = $_GET["login"];
                                    $query = "SELECT nameGroup FROM group_user WHERE login=\"".$login."\"";
                                    // echo $query;
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                }
                            break;

                            
                            case 'getListUser':
                                // ''' SELECT login FROM user_group_n WHERE n_group={0} GROUP BY login;'''.format(group)
                                if (isset($_GET["nameGroup"])) {
                                    $nameGroup = $_GET["nameGroup"];
                                    $query = "SELECT login FROM group_user WHERE nameGroup=\"".$nameGroup."\" GROUP BY login";
                                    // echo $query;
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                }
                            break; 
                        }
                    break; 

                    
                    case 'insert':
                        // "insert into user_group_n (login, n_group) values ({0},{1});".format(login, n_group)
                        if (isset($_GET["login"]) && isset($_GET["nameGroup"])) {
                            $login = $_GET["login"];
                            $nameGroup = $_GET["nameGroup"];
                            $query = "INSERT INTO group_user VALUES(NULL, '".$login."', '".$nameGroup."')";
                            $server = new ServerResponse($query, $typeQuery);
                        }
                    break;
                    

                    case 'update':
                            // if (isset($_GET["login"]) && isset($_GET["password"])){
                            //     $login = $_GET["login"];
                            //     $password = $_GET["password"]; 
                            //     $query = "UPDATE chanel SET password=".$password." WHERE login=".$login;
                            //     $server = new ServerResponse($query, $typeQuery);
                            // }
                    break;
                    

                    case 'delete':
                        // $query = "DELETE FROM chanel WHERE id=2";
                        // $server = new ServerResponse($query, $typeQuery);
                    break;
                    

                    case 'clear':
                        $query = "DELETE FROM group_user";
                        $server = new ServerResponse($query, $typeQuery);
                    break;

                }
        break;
        









        case 'group_user_mess': 
                
                switch ($action) {
                    
                    case 'get':
                            
                       switch ($detalAction) {
                            
                            case 'getMessGroup':
                                // "select mess from mess_user_group_n where name_group={0};".format(name_group)
                                if (isset($_GET["nameGroup"])) {
                                    $nameGroup = $_GET["nameGroup"];
                                    $query = "SELECT mess FROM group_user_mess WHERE nameGroup=\"".$nameGroup."\"";
                                    // echo $query;
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                }
                            break;
                        } 
                    break;
                    

                    case 'insert':
                        // ""insert into mess_user_group_n (from_login, name_group, mess) values ({0},{1},{2});".format(from_login, name_group, mess)
                        if (isset($_GET["fromLogin"]) && isset($_GET["nameGroup"]) && isset($_GET["mess"])) {
                            
                            $fromLogin = $_GET["fromLogin"];
                            $nameGroup = $_GET["nameGroup"];
                            $mess = $_GET["mess"];
                            $query = "INSERT INTO group_user_mess VALUES(NULL, '".$fromLogin."', '".$nameGroup."', '".$mess."')";
                            $server = new ServerResponse($query, $typeQuery);

                        }
                    break;
                    

                    case 'update':
                            
                            // if (isset($_GET["login"]) && isset($_GET["password"])){
                            //     $login = $_GET["login"];
                            //     $password = $_GET["password"]; 
                            //     $query = "UPDATE chanel SET password=".$password." WHERE login=".$login;
                            //     $server = new ServerResponse($query, $typeQuery);
                            // }
                    break;
                    

                    case 'delete':
                        
                        // $query = "DELETE FROM chanel WHERE id=2";
                        // $server = new ServerResponse($query, $typeQuery);
                    break;
                    

                    case 'clear':
                        
                        $query = "DELETE FROM group_user_mess";
                        $server = new ServerResponse($query, $typeQuery);
                    break;
                    
                }
        break;
        









        case 'board_author': 
                
                switch ($action) {
                    
                    case 'get':
                            
                       
                        switch ($detalAction) {
                            
                            case 'getMyBoard':
                                // ''' SELECT nameboard FROM author_board WHERE login={0};'''.format(login)
                                if (isset($_GET["login"])) {
                                    $login = $_GET["login"];
                                    $query = "SELECT nameBoard FROM board_author WHERE login=\"".$login."\"";
                                    // echo $query;
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                }
                            break;

                        } 
                    break;
                    

                    case 'insert':
                        // ''' INSERT INTO author_board (login, nameboard) 
                        //VALUES ({0}, {1});'''.format(login, nameboard)
                        if (isset($_GET["login"]) && isset($_GET["nameBoard"])) {
                           $login = $_GET["login"];
                           $nameBoard = $_GET["nameBoard"];
                           $query = "INSERT INTO board_author VALUES(NULL, '".$login."', '".$nameBoard."')";
                           $server = new ServerResponse($query, $typeQuery);

                        }
                    break;
                    

                    case 'update':
                            // if (isset($_GET["login"]) && isset($_GET["password"])){
                            //     $login = $_GET["login"];
                            //     $password = $_GET["password"]; 
                            //     $query = "UPDATE chanel SET password=".$password." WHERE login=".$login;
                            //     $server = new ServerResponse($query, $typeQuery);
                            // }
                    break;
                    

                    case 'delete':
                        // ''' DELETE FROM author_board WHERE nameboard = {0};'''.format(nameboard)
                        if (isset($_GET["nameBoard"])) {
                            $nameBoard = $_GET["nameBoard"];
                            $query = "DELETE FROM board_author WHERE nameBoard='".$nameBoard."'";
                            $server = new ServerResponse($query, $typeQuery);
                        
                        }
                    break;
                    

                    case 'clear':
                        $query = "DELETE FROM board_author";
                        $server = new ServerResponse($query, $typeQuery);
                    break;
                }
        break;
        









        case 'board_manage_task': 
                
                switch ($action) {
                    
                    case 'get':
                            
                        switch ($detalAction) {
                            
                            case 'getListAuthorSubjectDateTask':
                                // '''SELECT author, subject, date FROM manage_task WHERE user={0} GROUP BY author, subject, date;'''.format(user)
                                if (isset($_GET["user"])) {
                                    $user = $_GET["user"];
                                    $query = "SELECT author, subject, date  FROM board_manage_task WHERE user=\"".$user."\" GROUP BY author, subject, date";
                                    // echo $query;
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                } 
                            break;


                            case 'getListTask':
                                // ''' SELECT task_user FROM manage_task  WHERE user={0} AND author={1} AND subject={2} AND date={3}; '''.format(user, author, subject, date)
                                if (isset($_GET["user"]) && isset($_GET["author"]) && isset($_GET["subject"]) && isset($_GET["date"])) {
                                    $user = $_GET["user"];
                                    $author = $_GET["author"];
                                    $subject = $_GET["subject"];
                                    $date = $_GET["date"];
                                    $query = "SELECT taskUser FROM board_manage_task WHERE user=\"".$user."\" AND author=\"".$author."\" AND subject=\"".$subject."\" AND date=\"".$date."\"";
                                    // echo $query;
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                } 
                            break;

                            // http://localhost/?part=board_manage_task&action=get&detalAction=getListTaskExec&author=author1&subject=subjec1&date=234:234&user=user1
                            case 'getListTaskExec':
                                // ''' SELECT exec FROM manage_task WHERE user={0} AND author={1} AND subject={2} AND date={3}; '''.format(user, author, subject, date)
                                if (isset($_GET["user"]) && isset($_GET["author"]) && isset($_GET["subject"]) && isset($_GET["date"])) {
                                    $user = $_GET["user"];
                                    $author = $_GET["author"];
                                    $subject = $_GET["subject"];
                                    $date = $_GET["date"];
                                    $query = "SELECT exec FROM board_manage_task WHERE user=\"".$user."\" AND author=\"".$author."\" AND subject=\"".$subject."\" AND date=\"".$date."\"";
                                    // echo $query;
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                } 
                            break;

                            //// 2 новых запроса
                            // http://localhost/?part=board_manage_task&action=get&detalAction=getAuthorSubjectDate&author=author1
                            case 'getAuthorSubjectDate':
                                if (isset($_GET["author"])) {
                                    $author = $_GET["author"];
                                    $query = "SELECT author, subject, date FROM board_manage_task WHERE author=\"".$author."\" GROUP BY author, subject, date";
                                    // echo $query;
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                } 
                            break;


                            // http://localhost/?part=board_manage_task&action=get&detalAction=getListUser&author=author1&subject=subjec1&date=234:234
                            case 'getListUser':
                                if (isset($_GET["author"]) && isset($_GET["subject"]) && isset($_GET["date"])) {
                                    $author = $_GET["author"];
                                    $subject = $_GET["subject"];
                                    $date = $_GET["date"];
                                    $query = "SELECT user FROM board_manage_task WHERE author=\"".$author."\" AND subject=\"".$subject."\" AND date=\"".$date."\" GROUP BY user";
                                    // echo $query;
                                    $server = new ServerResponse($query, $typeQuery);
                                    echo $server->getResponse();                         
                                } 
                            break;

                        } 
                    break;

 
                    case 'insert':
                        // ''' INSERT INTO manage_task (author, subject, date, user, task_user, exec) VALUES ({0}, {1}, {2}, {3}, {4}, 0); '''.format(author, subject, date, user, task_user)
                        if (isset($_GET["author"]) && isset($_GET["subject"]) && isset($_GET["date"]) &&
                            isset($_GET["user"]) && isset($_GET["taskUser"]) && isset($_GET["exec"])) {
                            $author = $_GET["author"];
                            $subject = $_GET["subject"];
                            $date = $_GET["date"];
                            $user = $_GET["user"];
                            $taskUser = $_GET["taskUser"];
                            $exec = $_GET["exec"];
                            $query = "INSERT INTO board_manage_task VALUES(NULL, '".$author."', '".$subject."', '".$date."', '".$user."', '".$taskUser."', ".$exec.")";
                            $server = new ServerResponse($query, $typeQuery);

                        }
                    break;
                    

                    case 'update':
                            
                             if (isset($_GET["author"]) && isset($_GET["subject"]) && isset($_GET["date"]) &&
                                isset($_GET["user"]) && isset($_GET["taskUser"]) && isset($_GET["exec"])){
                                $author = $_GET["author"];
                                $subject = $_GET["subject"];
                                $date = $_GET["date"];
                                $user = $_GET["user"];
                                $taskUser = $_GET["taskUser"];
                                $exec = $_GET["exec"];
                                $query = "UPDATE board_manage_task SET exec=".$exec." WHERE author='".$author."' AND subject='".$subject."' AND date='".$date."' AND user='".$user."' AND taskUser='".$taskUser."'";
                                $server = new ServerResponse($query, $typeQuery);
                             }
                    break;
                    

                    case 'delete':
                        // ''' DELETE FROM author_board WHERE nameboard = {0};'''.format(nameboard)
                        if (isset($_GET["nameBoard"])) {
                            $nameBoard = $_GET["nameBoard"];
                            $query = "DELETE FROM board_author WHERE nameBoard='".$nameBoard."'";
                            $server = new ServerResponse($query, $typeQuery);
                        
                        }
                    break;
                    

                    case 'clear':
                        $query = "DELETE FROM board_manage_task";
                        $server = new ServerResponse($query, $typeQuery);
                    break;
                }
        break;       
    }
   
?>
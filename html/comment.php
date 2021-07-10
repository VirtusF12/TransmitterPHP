

<?php
/*

$link = mysqli_connect($host, $user, $password, $database) 
    or die("Ошибка -> " . mysqli_error($link)); 
echo "<h1>Соединение с БД открыто</h1><br><br>";     

//$device = $_GET['device'];
//$brand = $_GET['brand'];

// http://127.0.0.2/index.php?brand=%22Meizu%22
$query = "	SELECT device, brand
 			FROM phones
 			WHERE brand =".$brand.";";
// запрос на получение всех записей			
 $query_select = " SELECT * 
                FROM phones;";
echo "Запрос на получение(всех): ".$query_test."<br><br><br>";
// запрос на вставку 
$nameDevice = "device111";
$nameBrand = "brand1TEST";
$query_insert = "INSERT INTO phones 
                VALUES (NULL, '".$nameDevice."','".$nameBrand."')";
// пример: $query ="INSERT INTO phones VALUES(NULL, 'Samsung Galaxy III','Samsumg')";
echo "Запрос на вставку: ".$query_insert."<br>";
// запрос на удаление строки
$query_delete = "DELETE FROM phones WHERE id=17";
// запрос на очистку всей таблицы
$query_clear = "DELETE FROM phones";
// запрос на обновление 
$query_update = "UPDATE phones SET device='new_device', brand='new_brand' 
                    WHERE id=19";

$num = 0;

switch($num){
    case 0: 
            $result = mysqli_query($link, $query_insert) or die("Ошибка " . mysqli_error($link)); 
            if ($result) {
                echo "<span style='color:blue;'>Данные добавлены</span>";
            } 
            echo '<h2>ветвь 0</h2><br>';
            ;break;

    case 1:
            $result = mysqli_query($link, $query_select) or die("Ошибка ->" . mysqli_error($link)); 
           
            if($result) {
           
                $rows = mysqli_num_rows($result); // количество полученных строк
                for ($i = 0; $i < $rows; $i++){
                    
                    $row = mysqli_fetch_row($result);
                    echo '(';
                    for ($j = 0; $j < 3; $j++){
                        echo '\''.$row[$j].'\'';
                    }
                    echo ')';
                }
                mysqli_free_result($result);
            } 
            echo '<br><br><h2>ветвь 1</h2><br>';
            ;break;
    case 2:
            $result = mysqli_query($link, $query_update) or die("Ошибка " . mysqli_error($link)); 
            if ($result) {
                echo "<span style='color:blue;'>Данные были обновлены</span>";
            } 
            echo '<h2>ветвь 2</h2><br>';
            ;break;
    case 3:
            $result = mysqli_query($link, $query_delete) or die("Ошибка " . mysqli_error($link)); 
            if ($result) {
                echo "<span style='color:blue;'>Строка была удалена</span>";
            } 
            echo '<h2>ветвь 3</h2><br>';
            ;break;
    case 4:
            $result = mysqli_query($link, $query_clear) or die("Ошибка " . mysqli_error($link)); 
            if ($result) {
                echo "<span style='color:blue;'>Таблица очищена</span>";
            } 
            echo '<h2>ветвь 4</h2><br>';
            ;break;

}
mysqli_close($link);
echo "<h1>Закрытие соединения с БД</h1>";
*/


//~~~~~~~~~~~~~~~~~~~~~~~~~
        /*
        case "phones": 
                switch ($action) {
                    case 'get':
                        $query = "SELECT * FROM phones";
                        $server = new ServerResponse($query, $typeQuery);
                        break;
                    //------------- 
                    case 'insert':
                        $query = "INSERT INTO phones VALUES(NULL, 'Samsung Galaxy III','Samsumg')";
                        $server = new ServerResponse($query, $typeQuery);
                    break;
                    //-------------
                    case 'update':
                    $query = "UPDATE phones SET device='new_device', brand='new_brand' 
                    WHERE id=19";
                        $server = new ServerResponse($query, $typeQuery);
                        break;
                    //-------------
                    case 'delete':
                        $query = "DELETE FROM phones WHERE id=17";
                        $server = new ServerResponse($query, $typeQuery);
                        break;
                    //-------------
                    case 'clear':
                        $query = "DELETE FROM phones";
                        $server = new ServerResponse($query, $typeQuery);
                        break;
                    //-------------
                }
        ;break;
        */
        //~~~~~~~~~~~~~~~~~~~~~~~~~

<!--
из ветви1 

                /* 
                echo "<table><tr><th>Id</th><th>Модель</th><th>Производитель</th></tr>";
                for ($i = 0 ; $i < $rows ; ++$i)
                {
                    $row = mysqli_fetch_row($result);
                    echo "<tr>";
                        for ($j = 0 ; $j < 3 ; ++$j) 
                            echo "<td>$row[$j]</td>";
                    echo "</tr>";
                }
                echo "</table>";
                */
                // очищаем результат

 -->

?>
package ASCII;

import java.util.HashMap;
import java.util.Map;

/**
 * Сопоставление числа с буквой
 * @author Mihail Kovalenko
 */
public final class ASCII {

    public static Map<Integer, Character> token;

    static {
        token = new HashMap<>();

        // LETTERS LOW REG EN (a,b,c, ...)
        token.put(0,'a'); // 0
        token.put(1,'b'); // 1
        token.put(2,'c'); // 2
        token.put(3,'d'); // 3
        token.put(4,'e'); // 4
        token.put(5,'f'); // 5
        token.put(6,'g'); // 6
        token.put(7,'h'); // 7
        token.put(8,'i'); // 8
        token.put(9,'j'); // 9

        token.put(10,'k'); // 10
        token.put(11,'l'); // 11
        token.put(12,'m'); // 12
        token.put(13,'n'); // 13
        token.put(14,'o'); // 14
        token.put(15,'p'); // 15
        token.put(16,'q'); // 16
        token.put(17,'r'); // 17
        token.put(18,'s'); // 18
        token.put(19,'t'); // 19

        token.put(20,'u'); // 20
        token.put(21,'v'); // 21
        token.put(22,'w'); // 22
        token.put(23,'x'); // 23
        token.put(24,'y'); // 24
        token.put(25,'z'); // 25

        // LETTERS HIGHT EN (A,B,C, ...)
        token.put(26,'A'); // 26
        token.put(27,'B'); // 27
        token.put(28,'C'); // 28
        token.put(29,'D'); // 29
        token.put(30,'E'); // 30
        token.put(31,'F'); // 31
        token.put(32,'G'); // 32
        token.put(33,'H'); // 33
        token.put(34,'I'); // 34
        token.put(35,'J'); // 35

        token.put(36,'K'); // 36
        token.put(37,'L'); // 37
        token.put(38,'M'); // 38
        token.put(39,'N'); // 39
        token.put(40,'O'); // 40
        token.put(41,'P'); // 41
        token.put(42,'Q'); // 42
        token.put(43,'R'); // 43
        token.put(44,'S'); // 44
        token.put(45,'T'); // 45

        token.put(46,'U'); // 46
        token.put(47,'V'); // 47
        token.put(48,'W'); // 48
        token.put(49,'X'); // 49
        token.put(50,'Y'); // 50
        token.put(51,'Z'); // 51


        // LETTERS LOW RUS (а,б,в, ...)
        token.put(52,'а'); // 52
        token.put(53,'б'); // 53
        token.put(54,'в'); // 54
        token.put(55,'г'); // 55
        token.put(56,'д'); // 56
        token.put(57,'е'); // 57
        token.put(58,'ё'); // 58
        token.put(59,'ж'); // 59
        token.put(60,'з'); // 60
        token.put(61,'и'); // 61

        token.put(62,'й'); // 62
        token.put(63,'к'); // 63
        token.put(64,'л'); // 64
        token.put(65,'м'); // 65
        token.put(66,'н'); // 66
        token.put(67,'о'); // 67
        token.put(68,'п'); // 68
        token.put(69,'р'); // 69
        token.put(70,'с'); // 70
        token.put(71,'т'); // 71

        token.put(72,'у'); // 72
        token.put(73,'ф'); // 73
        token.put(74,'х'); // 74
        token.put(75,'ц'); // 75
        token.put(76,'ч'); // 76
        token.put(77,'ш'); // 77
        token.put(78,'щ'); // 78
        token.put(79,'ъ'); // 79
        token.put(80,'ы'); // 80
        token.put(81,'ь'); // 81

        token.put(82,'э'); // 82
        token.put(83,'ю'); // 83
        token.put(84,'я'); // 84

        // LETTERS HIGHT RUS (А,Б,В, ...)
        token.put(85,'А'); // 85
        token.put(86,'Б'); // 86
        token.put(87,'В'); // 87
        token.put(88,'Г'); // 88
        token.put(89,'Д'); // 89
        token.put(90,'Е'); // 90
        token.put(91,'Ё'); // 91
        token.put(92,'Ж'); // 92
        token.put(93,'З'); // 93
        token.put(94,'И'); // 94

        token.put(95,'Й'); // 95
        token.put(96,'К'); // 96
        token.put(97,'Л'); // 97
        token.put(98,'М'); // 98
        token.put(99,'Н'); // 99
        token.put(100,'О'); // 100
        token.put(101,'П'); // 101
        token.put(102,'Р'); // 102
        token.put(103,'С'); // 103
        token.put(104,'Т'); // 104

        token.put(105,'У'); // 105
        token.put(106,'Ф'); // 106
        token.put(107,'Х'); // 107
        token.put(108,'Ц'); // 108
        token.put(109,'Ч'); // 109
        token.put(110,'Ш'); // 110
        token.put(111,'Щ'); // 111
        token.put(112,'Ъ'); // 112
        token.put(113,'Ы'); // 113
        token.put(114,'Ь'); // 114

        token.put(115,'Э'); // 115
        token.put(116,'Ю'); // 116
        token.put(117,'Я'); // 117

        // NUMBER
        token.put(118,'0'); // 118
        token.put(119,'1'); // 119
        token.put(120,'2'); // 120
        token.put(121,'3'); // 121
        token.put(122,'4'); // 122
        token.put(123,'5'); // 123
        token.put(124,'6'); // 124
        token.put(125,'7'); // 125
        token.put(126,'8'); // 126
        token.put(127,'9'); // 127

        // SIMBOLS
        token.put(128,'`'); // 128
        token.put(129,'~'); // 129
        token.put(130,'!'); // 130
        token.put(131,'@'); // 131
        token.put(132,'#'); // 132
        token.put(133,'$'); // 133
        token.put(134,'%'); // 134
        token.put(135,'^'); // 135
        token.put(136,'&'); // 136
        token.put(137,'*'); // 137

        token.put(138,'('); // 138
        token.put(139,')'); // 139
        token.put(140,'_'); // 140
        token.put(141,'-'); // 141
        token.put(142,'+'); // 142
        token.put(143,'='); // 143
        token.put(144,' '); // 144
        token.put(145,'{'); // 145
        token.put(146,'}'); // 146
        token.put(147,'['); // 147

        token.put(148,']'); // 148
        token.put(149,'/'); // 149
        token.put(150,'№'); // 150
        token.put(151,';'); // 151
        token.put(152,':'); // 152
        token.put(153,'?'); // 153
        token.put(154,','); // 154
        token.put(155,'.'); // 155
        token.put(156,'<'); // 156
        token.put(157,'>'); // 157

    }
    public static int getSizeTableASCII(){
        return token.size();
    }
}

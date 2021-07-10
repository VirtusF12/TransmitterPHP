package Parser;

/**
 * Enum - форматов ответов от сервера
 * @author Mihail Kovalenko
 */
public enum FormatTextServer {

    FORMAT1, // c regexpr (u'lex1', u'hb', u'18:1:2018_24:2:2018')(u'lex1', u'hh', u'18:1:2018_4:1:2018')
    FORMAT2, // (u'admin',)(u'dew',)(u'er1',)(u'lex1',)(u'lex2',)(u'mih',)(u'ter',)(u'test',)
    FORMAT3, // (u'lex1', u'14_kb', u'12:33')(u'lex1', u'14_kb', u'12:33')
    FORMAT4, // (0,)(0,)(0,) -> '0,)'0,)'0,)
    FORMAT5, // ﻿ ﻿﻿	('admin')('test1')('test2')('mih')('mih')('mih')('weqwer')
    NONE    // without regexpr (u'20`8`9`9',)(u'8`10`10`14',)(u'9`10`10',)

}

grammar SQL;

parse :
    sql_stmt_list ;

sql_stmt_list :
    ';'* sql_stmt ( ';'+ sql_stmt )* ';'* ;

sql_stmt :
    create_table_stmt    # createTableStatement
    | create_db_stmt     # createDatabaseStatement
    | drop_db_stmt       # dropDatabaseStatement
    | delete_stmt        # deleteStatement
    | drop_table_stmt    # dropTableStatement
    | insert_stmt        # insertStatement
    | select_stmt        # selectStatement
    | use_db_stmt        # useStatement
    | show_db_stmt       # showDatabaseStatement
    | show_table_stmt    # showTableStatement
    | quit_stmt          # quitStatement
    | update_stmt        # updateStatement ;

create_db_stmt :
    K_CREATE K_DATABASE database_name ;

drop_db_stmt :
    K_DROP K_DATABASE ( K_IF K_EXISTS )? database_name ;

create_table_stmt :
    K_CREATE K_TABLE table_name
        '(' column_def ( ',' column_def )* ( ',' table_constraint )? ')' ;

use_db_stmt :
    K_USE database_name;

delete_stmt :
    K_DELETE K_FROM table_name ( K_WHERE condition )? ;

drop_table_stmt :
    K_DROP K_TABLE ( K_IF K_EXISTS )? table_name ;

show_db_stmt :
    K_SHOW K_DATABASES;

quit_stmt :
    K_QUIT;

show_table_stmt :
    K_SHOW K_TABLES database_name;

insert_stmt :
    K_INSERT K_INTO table_name ( '(' column_name ( ',' column_name )* ')' )?
        K_VALUES value_entry ( ',' value_entry )* ;

value_entry :
    '(' literal_value ( ',' literal_value )* ')' ;

select_stmt :
   select_core ( K_ORDER K_BY column_name ( K_ASC | K_DESC )? )? ;

select_core :
    K_SELECT ( K_DISTINCT | K_ALL )? result_column ( ',' result_column )*
        K_FROM table_query ( ',' table_query )* ( K_WHERE condition )? ;

update_stmt :
    K_UPDATE table_name
        K_SET column_name '=' expression ( K_WHERE condition )? ;

column_def :
    column_name type_name column_constraint* ;

type_name :
    T_INT                              # typeInt
    | T_LONG                           # typeLong
    | T_FLOAT                          # typeFloat
    | T_DOUBLE                         # typeDouble
    | T_STRING '(' NUMERIC_LITERAL ')' # typeString ;

column_constraint :
    K_PRIMARY K_KEY     # primaryKeyConstraint
    | K_NOT K_NULL      # notNullConstraint ;

condition :
    expression comparator expression;

comparer :
    column_full_name
    | literal_value ;

comparator :
    EQ | NE | LE | GE | LT | GT ;

expression :
    comparer
    | expression ( MUL | DIV ) expression
    | expression ( ADD | SUB ) expression
    | '(' expression ')';

table_constraint :
    K_PRIMARY K_KEY '(' column_name (',' column_name)* ')' ;

result_column
    : '*'
    | table_name '.' '*'
    | column_full_name;

table_query :
    table_name ( K_JOIN table_name K_ON condition )? ;

literal_value :
    NUMERIC_LITERAL
    | STRING_LITERAL
    | K_NULL ;

database_name :
    IDENTIFIER ;

table_name :
    IDENTIFIER ;

column_full_name:
    ( table_name '.' )? column_name ;

column_name :
    IDENTIFIER ;

EQ : '=';
NE : '<>';
LT : '<';
GT : '>';
LE : '<=';
GE : '>=';

ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';

T_INT : I N T;
T_LONG : L O N G;
T_FLOAT : F L O A T;
T_DOUBLE : D O U B L E;
T_STRING : S T R I N G;

K_ADD : A D D;
K_ASC : A S C;
K_ALL : A L L;
K_BY : B Y;
K_COLUMN : C O L U M N;
K_CREATE : C R E A T E;
K_DATABASE : D A T A B A S E;
K_DATABASES : D A T A B A S E S;
K_DELETE : D E L E T E;
K_DESC : D E C S;
K_DISTINCT : D I S T I N C T;
K_DROP : D R O P;
K_EXISTS : E X I S T S;
K_FROM : F R O M;
K_IF : I F;
K_INSERT : I N S E R T;
K_INTO : I N T O;
K_JOIN : J O I N;
K_KEY : K E Y;
K_NOT : N O T;
K_NULL : N U L L;
K_ON : O N;
K_ORDER : O R D E R;
K_PRIMARY : P R I M A R Y;
K_QUIT : Q U I T;
K_SELECT : S E L E C T;
K_SET : S E T;
K_SHOW : S H O W;
K_TABLE : T A B L E;
K_TABLES : T A B L E S;
K_UPDATE : U P D A T E;
K_USE : U S E;
K_VALUES : V A L U E S;
K_WHERE : W H E R E;

IDENTIFIER :
    [a-zA-Z_] [a-zA-Z_0-9]* ;

NUMERIC_LITERAL :
    DIGIT+ EXPONENT?
    | DIGIT+ '.' DIGIT* EXPONENT?
    | '.' DIGIT+ EXPONENT? ;

EXPONENT :
    E [-+]? DIGIT+ ;

STRING_LITERAL :
    '\'' ( ~'\'' | '\'\'' )* '\'' ;

SINGLE_LINE_COMMENT :
    '--' ~[\r\n]* -> channel(HIDDEN) ;

MULTILINE_COMMENT :
    '/*' .*? ( '*/' | EOF ) -> channel(HIDDEN) ;

SPACES :
    [ \u000B\t\r\n] -> channel(HIDDEN) ;

fragment DIGIT : [0-9] ;
fragment A : [aA] ;
fragment B : [bB] ;
fragment C : [cC] ;
fragment D : [dD] ;
fragment E : [eE] ;
fragment F : [fF] ;
fragment G : [gG] ;
fragment H : [hH] ;
fragment I : [iI] ;
fragment J : [jJ] ;
fragment K : [kK] ;
fragment L : [lL] ;
fragment M : [mM] ;
fragment N : [nN] ;
fragment O : [oO] ;
fragment P : [pP] ;
fragment Q : [qQ] ;
fragment R : [rR] ;
fragment S : [sS] ;
fragment T : [tT] ;
fragment U : [uU] ;
fragment V : [vV] ;
fragment W : [wW] ;
fragment X : [xX] ;
fragment Y : [yY] ;
fragment Z : [zZ] ;
// Generated from SQL.g4 by ANTLR 4.7.2
package parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T_INT=13, T_LONG=14, T_FLOAT=15, T_DOUBLE=16, 
		T_STRING=17, K_ADD=18, K_ASC=19, K_ALL=20, K_BY=21, K_COLUMN=22, K_CREATE=23, 
		K_DATABASE=24, K_DATABASES=25, K_DELETE=26, K_DESC=27, K_DISTINCT=28, 
		K_DROP=29, K_EXISTS=30, K_FROM=31, K_IF=32, K_INSERT=33, K_INTO=34, K_JOIN=35, 
		K_KEY=36, K_NOT=37, K_NULL=38, K_ON=39, K_ORDER=40, K_PRIMARY=41, K_QUIT=42, 
		K_SAVE=43, K_SELECT=44, K_SET=45, K_SHOW=46, K_TABLE=47, K_TABLES=48, 
		K_UPDATE=49, K_USE=50, K_VALUES=51, K_WHERE=52, IDENTIFIER=53, INTEGER=54, 
		NUMERIC_LITERAL=55, STRING_LITERAL=56, SINGLE_LINE_COMMENT=57, MULTILINE_COMMENT=58, 
		SPACES=59, UNEXPECTED_CHAR=60;
	public static final int
		RULE_parse = 0, RULE_error = 1, RULE_sql_stmt_list = 2, RULE_sql_stmt = 3, 
		RULE_create_db_stmt = 4, RULE_drop_db_stmt = 5, RULE_create_table_stmt = 6, 
		RULE_use_db_stmt = 7, RULE_delete_stmt = 8, RULE_drop_table_stmt = 9, 
		RULE_show_db_stmt = 10, RULE_quit_stmt = 11, RULE_show_table_stmt = 12, 
		RULE_insert_stmt = 13, RULE_value_entry = 14, RULE_save_stmt = 15, RULE_select_stmt = 16, 
		RULE_select_core = 17, RULE_update_stmt = 18, RULE_column_def = 19, RULE_type_name = 20, 
		RULE_column_constraint = 21, RULE_expr = 22, RULE_table_constraint = 23, 
		RULE_result_column = 24, RULE_table_query = 25, RULE_literal_value = 26, 
		RULE_database_name = 27, RULE_table_name = 28, RULE_column_full_name = 29, 
		RULE_column_name = 30, RULE_any_name = 31;
	private static String[] makeRuleNames() {
		return new String[] {
			"parse", "error", "sql_stmt_list", "sql_stmt", "create_db_stmt", "drop_db_stmt", 
			"create_table_stmt", "use_db_stmt", "delete_stmt", "drop_table_stmt", 
			"show_db_stmt", "quit_stmt", "show_table_stmt", "insert_stmt", "value_entry", 
			"save_stmt", "select_stmt", "select_core", "update_stmt", "column_def", 
			"type_name", "column_constraint", "expr", "table_constraint", "result_column", 
			"table_query", "literal_value", "database_name", "table_name", "column_full_name", 
			"column_name", "any_name"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'('", "','", "')'", "'='", "'<'", "'<='", "'>'", "'>='", 
			"'<>'", "'*'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "T_INT", "T_LONG", "T_FLOAT", "T_DOUBLE", "T_STRING", "K_ADD", 
			"K_ASC", "K_ALL", "K_BY", "K_COLUMN", "K_CREATE", "K_DATABASE", "K_DATABASES", 
			"K_DELETE", "K_DESC", "K_DISTINCT", "K_DROP", "K_EXISTS", "K_FROM", "K_IF", 
			"K_INSERT", "K_INTO", "K_JOIN", "K_KEY", "K_NOT", "K_NULL", "K_ON", "K_ORDER", 
			"K_PRIMARY", "K_QUIT", "K_SAVE", "K_SELECT", "K_SET", "K_SHOW", "K_TABLE", 
			"K_TABLES", "K_UPDATE", "K_USE", "K_VALUES", "K_WHERE", "IDENTIFIER", 
			"INTEGER", "NUMERIC_LITERAL", "STRING_LITERAL", "SINGLE_LINE_COMMENT", 
			"MULTILINE_COMMENT", "SPACES", "UNEXPECTED_CHAR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ParseContext extends ParserRuleContext {
		public Sql_stmt_listContext sql_stmt_list() {
			return getRuleContext(Sql_stmt_listContext.class,0);
		}
		public ErrorContext error() {
			return getRuleContext(ErrorContext.class,0);
		}
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitParse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case K_CREATE:
			case K_DELETE:
			case K_DROP:
			case K_INSERT:
			case K_QUIT:
			case K_SAVE:
			case K_SELECT:
			case K_SHOW:
			case K_UPDATE:
			case K_USE:
				{
				setState(64);
				sql_stmt_list();
				}
				break;
			case UNEXPECTED_CHAR:
				{
				setState(65);
				error();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ErrorContext extends ParserRuleContext {
		public Token UNEXPECTED_CHAR;
		public TerminalNode UNEXPECTED_CHAR() { return getToken(SQLParser.UNEXPECTED_CHAR, 0); }
		public ErrorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_error; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitError(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ErrorContext error() throws RecognitionException {
		ErrorContext _localctx = new ErrorContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_error);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			((ErrorContext)_localctx).UNEXPECTED_CHAR = match(UNEXPECTED_CHAR);
			 throw new RuntimeException("UNEXPECTED_CHAR=" + (((ErrorContext)_localctx).UNEXPECTED_CHAR!=null?((ErrorContext)_localctx).UNEXPECTED_CHAR.getText():null)); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sql_stmt_listContext extends ParserRuleContext {
		public List<Sql_stmtContext> sql_stmt() {
			return getRuleContexts(Sql_stmtContext.class);
		}
		public Sql_stmtContext sql_stmt(int i) {
			return getRuleContext(Sql_stmtContext.class,i);
		}
		public Sql_stmt_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_stmt_list; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitSql_stmt_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sql_stmt_listContext sql_stmt_list() throws RecognitionException {
		Sql_stmt_listContext _localctx = new Sql_stmt_listContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_sql_stmt_list);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(71);
				match(T__0);
				}
				}
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(77);
			sql_stmt();
			setState(86);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(79); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(78);
						match(T__0);
						}
						}
						setState(81); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==T__0 );
					setState(83);
					sql_stmt();
					}
					} 
				}
				setState(88);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(89);
				match(T__0);
				}
				}
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sql_stmtContext extends ParserRuleContext {
		public Sql_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_stmt; }
	 
		public Sql_stmtContext() { }
		public void copyFrom(Sql_stmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CreateDatabaseStatementContext extends Sql_stmtContext {
		public Create_db_stmtContext create_db_stmt() {
			return getRuleContext(Create_db_stmtContext.class,0);
		}
		public CreateDatabaseStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCreateDatabaseStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ShowDatabaseStatementContext extends Sql_stmtContext {
		public Show_db_stmtContext show_db_stmt() {
			return getRuleContext(Show_db_stmtContext.class,0);
		}
		public ShowDatabaseStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitShowDatabaseStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class QuitStatementContext extends Sql_stmtContext {
		public Quit_stmtContext quit_stmt() {
			return getRuleContext(Quit_stmtContext.class,0);
		}
		public QuitStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitQuitStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UseStatementContext extends Sql_stmtContext {
		public Use_db_stmtContext use_db_stmt() {
			return getRuleContext(Use_db_stmtContext.class,0);
		}
		public UseStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitUseStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CreateTableStatementContext extends Sql_stmtContext {
		public Create_table_stmtContext create_table_stmt() {
			return getRuleContext(Create_table_stmtContext.class,0);
		}
		public CreateTableStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCreateTableStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SaveStatementContext extends Sql_stmtContext {
		public Save_stmtContext save_stmt() {
			return getRuleContext(Save_stmtContext.class,0);
		}
		public SaveStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitSaveStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ShowTableStatementContext extends Sql_stmtContext {
		public Show_table_stmtContext show_table_stmt() {
			return getRuleContext(Show_table_stmtContext.class,0);
		}
		public ShowTableStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitShowTableStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DropDatabaseStatementContext extends Sql_stmtContext {
		public Drop_db_stmtContext drop_db_stmt() {
			return getRuleContext(Drop_db_stmtContext.class,0);
		}
		public DropDatabaseStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitDropDatabaseStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DeleteStatementContext extends Sql_stmtContext {
		public Delete_stmtContext delete_stmt() {
			return getRuleContext(Delete_stmtContext.class,0);
		}
		public DeleteStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitDeleteStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class InsertStatementContext extends Sql_stmtContext {
		public Insert_stmtContext insert_stmt() {
			return getRuleContext(Insert_stmtContext.class,0);
		}
		public InsertStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitInsertStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UpdateStatementContext extends Sql_stmtContext {
		public Update_stmtContext update_stmt() {
			return getRuleContext(Update_stmtContext.class,0);
		}
		public UpdateStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitUpdateStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DropTableStatementContext extends Sql_stmtContext {
		public Drop_table_stmtContext drop_table_stmt() {
			return getRuleContext(Drop_table_stmtContext.class,0);
		}
		public DropTableStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitDropTableStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SelectStatementContext extends Sql_stmtContext {
		public Select_stmtContext select_stmt() {
			return getRuleContext(Select_stmtContext.class,0);
		}
		public SelectStatementContext(Sql_stmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitSelectStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sql_stmtContext sql_stmt() throws RecognitionException {
		Sql_stmtContext _localctx = new Sql_stmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_sql_stmt);
		try {
			setState(108);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				_localctx = new CreateTableStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(95);
				create_table_stmt();
				}
				break;
			case 2:
				_localctx = new CreateDatabaseStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(96);
				create_db_stmt();
				}
				break;
			case 3:
				_localctx = new DropDatabaseStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(97);
				drop_db_stmt();
				}
				break;
			case 4:
				_localctx = new DeleteStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(98);
				delete_stmt();
				}
				break;
			case 5:
				_localctx = new DropTableStatementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(99);
				drop_table_stmt();
				}
				break;
			case 6:
				_localctx = new InsertStatementContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(100);
				insert_stmt();
				}
				break;
			case 7:
				_localctx = new SaveStatementContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(101);
				save_stmt();
				}
				break;
			case 8:
				_localctx = new SelectStatementContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(102);
				select_stmt();
				}
				break;
			case 9:
				_localctx = new UseStatementContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(103);
				use_db_stmt();
				}
				break;
			case 10:
				_localctx = new ShowDatabaseStatementContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(104);
				show_db_stmt();
				}
				break;
			case 11:
				_localctx = new ShowTableStatementContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(105);
				show_table_stmt();
				}
				break;
			case 12:
				_localctx = new QuitStatementContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(106);
				quit_stmt();
				}
				break;
			case 13:
				_localctx = new UpdateStatementContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(107);
				update_stmt();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_db_stmtContext extends ParserRuleContext {
		public TerminalNode K_CREATE() { return getToken(SQLParser.K_CREATE, 0); }
		public TerminalNode K_DATABASE() { return getToken(SQLParser.K_DATABASE, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Create_db_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_db_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCreate_db_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Create_db_stmtContext create_db_stmt() throws RecognitionException {
		Create_db_stmtContext _localctx = new Create_db_stmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_create_db_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(K_CREATE);
			setState(111);
			match(K_DATABASE);
			setState(112);
			database_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Drop_db_stmtContext extends ParserRuleContext {
		public TerminalNode K_DROP() { return getToken(SQLParser.K_DROP, 0); }
		public TerminalNode K_DATABASE() { return getToken(SQLParser.K_DATABASE, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Drop_db_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drop_db_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitDrop_db_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Drop_db_stmtContext drop_db_stmt() throws RecognitionException {
		Drop_db_stmtContext _localctx = new Drop_db_stmtContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_drop_db_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(K_DROP);
			setState(115);
			match(K_DATABASE);
			setState(116);
			database_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_table_stmtContext extends ParserRuleContext {
		public TerminalNode K_CREATE() { return getToken(SQLParser.K_CREATE, 0); }
		public TerminalNode K_TABLE() { return getToken(SQLParser.K_TABLE, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public List<Column_defContext> column_def() {
			return getRuleContexts(Column_defContext.class);
		}
		public Column_defContext column_def(int i) {
			return getRuleContext(Column_defContext.class,i);
		}
		public Table_constraintContext table_constraint() {
			return getRuleContext(Table_constraintContext.class,0);
		}
		public Create_table_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_table_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitCreate_table_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Create_table_stmtContext create_table_stmt() throws RecognitionException {
		Create_table_stmtContext _localctx = new Create_table_stmtContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_create_table_stmt);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			match(K_CREATE);
			setState(119);
			match(K_TABLE);
			setState(120);
			table_name();
			setState(121);
			match(T__1);
			setState(122);
			column_def();
			setState(127);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(123);
					match(T__2);
					setState(124);
					column_def();
					}
					} 
				}
				setState(129);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(130);
				match(T__2);
				setState(131);
				table_constraint();
				}
			}

			setState(134);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Use_db_stmtContext extends ParserRuleContext {
		public TerminalNode K_USE() { return getToken(SQLParser.K_USE, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Use_db_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_use_db_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitUse_db_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Use_db_stmtContext use_db_stmt() throws RecognitionException {
		Use_db_stmtContext _localctx = new Use_db_stmtContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_use_db_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(K_USE);
			setState(137);
			database_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Delete_stmtContext extends ParserRuleContext {
		public TerminalNode K_DELETE() { return getToken(SQLParser.K_DELETE, 0); }
		public TerminalNode K_FROM() { return getToken(SQLParser.K_FROM, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_WHERE() { return getToken(SQLParser.K_WHERE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Delete_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitDelete_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Delete_stmtContext delete_stmt() throws RecognitionException {
		Delete_stmtContext _localctx = new Delete_stmtContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_delete_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(K_DELETE);
			setState(140);
			match(K_FROM);
			setState(141);
			table_name();
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHERE) {
				{
				setState(142);
				match(K_WHERE);
				setState(143);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Drop_table_stmtContext extends ParserRuleContext {
		public TerminalNode K_DROP() { return getToken(SQLParser.K_DROP, 0); }
		public TerminalNode K_TABLE() { return getToken(SQLParser.K_TABLE, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_IF() { return getToken(SQLParser.K_IF, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLParser.K_EXISTS, 0); }
		public Drop_table_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drop_table_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitDrop_table_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Drop_table_stmtContext drop_table_stmt() throws RecognitionException {
		Drop_table_stmtContext _localctx = new Drop_table_stmtContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_drop_table_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(K_DROP);
			setState(147);
			match(K_TABLE);
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_IF) {
				{
				setState(148);
				match(K_IF);
				setState(149);
				match(K_EXISTS);
				}
			}

			setState(152);
			table_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Show_db_stmtContext extends ParserRuleContext {
		public TerminalNode K_SHOW() { return getToken(SQLParser.K_SHOW, 0); }
		public TerminalNode K_DATABASES() { return getToken(SQLParser.K_DATABASES, 0); }
		public Show_db_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_show_db_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitShow_db_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Show_db_stmtContext show_db_stmt() throws RecognitionException {
		Show_db_stmtContext _localctx = new Show_db_stmtContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_show_db_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			match(K_SHOW);
			setState(155);
			match(K_DATABASES);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Quit_stmtContext extends ParserRuleContext {
		public TerminalNode K_QUIT() { return getToken(SQLParser.K_QUIT, 0); }
		public Quit_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quit_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitQuit_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Quit_stmtContext quit_stmt() throws RecognitionException {
		Quit_stmtContext _localctx = new Quit_stmtContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_quit_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			match(K_QUIT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Show_table_stmtContext extends ParserRuleContext {
		public TerminalNode K_SHOW() { return getToken(SQLParser.K_SHOW, 0); }
		public TerminalNode K_TABLES() { return getToken(SQLParser.K_TABLES, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Show_table_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_show_table_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitShow_table_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Show_table_stmtContext show_table_stmt() throws RecognitionException {
		Show_table_stmtContext _localctx = new Show_table_stmtContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_show_table_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(K_SHOW);
			setState(160);
			match(K_TABLES);
			setState(161);
			database_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Insert_stmtContext extends ParserRuleContext {
		public TerminalNode K_INSERT() { return getToken(SQLParser.K_INSERT, 0); }
		public TerminalNode K_INTO() { return getToken(SQLParser.K_INTO, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_VALUES() { return getToken(SQLParser.K_VALUES, 0); }
		public List<Value_entryContext> value_entry() {
			return getRuleContexts(Value_entryContext.class);
		}
		public Value_entryContext value_entry(int i) {
			return getRuleContext(Value_entryContext.class,i);
		}
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public Insert_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitInsert_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Insert_stmtContext insert_stmt() throws RecognitionException {
		Insert_stmtContext _localctx = new Insert_stmtContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_insert_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			match(K_INSERT);
			setState(164);
			match(K_INTO);
			setState(165);
			table_name();
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(166);
				match(T__1);
				setState(167);
				column_name();
				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(168);
					match(T__2);
					setState(169);
					column_name();
					}
					}
					setState(174);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(175);
				match(T__3);
				}
			}

			setState(179);
			match(K_VALUES);
			setState(180);
			value_entry();
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(181);
				match(T__2);
				setState(182);
				value_entry();
				}
				}
				setState(187);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Value_entryContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Value_entryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_entry; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitValue_entry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_entryContext value_entry() throws RecognitionException {
		Value_entryContext _localctx = new Value_entryContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_value_entry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			match(T__1);
			setState(189);
			expr(0);
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(190);
				match(T__2);
				setState(191);
				expr(0);
				}
				}
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(197);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Save_stmtContext extends ParserRuleContext {
		public TerminalNode K_SAVE() { return getToken(SQLParser.K_SAVE, 0); }
		public Save_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_save_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitSave_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Save_stmtContext save_stmt() throws RecognitionException {
		Save_stmtContext _localctx = new Save_stmtContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_save_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			match(K_SAVE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Select_stmtContext extends ParserRuleContext {
		public Select_coreContext select_core() {
			return getRuleContext(Select_coreContext.class,0);
		}
		public TerminalNode K_ORDER() { return getToken(SQLParser.K_ORDER, 0); }
		public TerminalNode K_BY() { return getToken(SQLParser.K_BY, 0); }
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public TerminalNode K_ASC() { return getToken(SQLParser.K_ASC, 0); }
		public TerminalNode K_DESC() { return getToken(SQLParser.K_DESC, 0); }
		public Select_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitSelect_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_stmtContext select_stmt() throws RecognitionException {
		Select_stmtContext _localctx = new Select_stmtContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_select_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			select_core();
			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_ORDER) {
				{
				setState(202);
				match(K_ORDER);
				setState(203);
				match(K_BY);
				setState(204);
				column_name();
				setState(206);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_ASC || _la==K_DESC) {
					{
					setState(205);
					_la = _input.LA(1);
					if ( !(_la==K_ASC || _la==K_DESC) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Select_coreContext extends ParserRuleContext {
		public TerminalNode K_SELECT() { return getToken(SQLParser.K_SELECT, 0); }
		public List<Result_columnContext> result_column() {
			return getRuleContexts(Result_columnContext.class);
		}
		public Result_columnContext result_column(int i) {
			return getRuleContext(Result_columnContext.class,i);
		}
		public TerminalNode K_FROM() { return getToken(SQLParser.K_FROM, 0); }
		public Table_queryContext table_query() {
			return getRuleContext(Table_queryContext.class,0);
		}
		public TerminalNode K_WHERE() { return getToken(SQLParser.K_WHERE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode K_DISTINCT() { return getToken(SQLParser.K_DISTINCT, 0); }
		public TerminalNode K_ALL() { return getToken(SQLParser.K_ALL, 0); }
		public Select_coreContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_core; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitSelect_core(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_coreContext select_core() throws RecognitionException {
		Select_coreContext _localctx = new Select_coreContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_select_core);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			match(K_SELECT);
			setState(212);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_ALL || _la==K_DISTINCT) {
				{
				setState(211);
				_la = _input.LA(1);
				if ( !(_la==K_ALL || _la==K_DISTINCT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(214);
			result_column();
			setState(219);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(215);
				match(T__2);
				setState(216);
				result_column();
				}
				}
				setState(221);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(222);
			match(K_FROM);
			setState(223);
			table_query();
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHERE) {
				{
				setState(224);
				match(K_WHERE);
				setState(225);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Update_stmtContext extends ParserRuleContext {
		public TerminalNode K_UPDATE() { return getToken(SQLParser.K_UPDATE, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_SET() { return getToken(SQLParser.K_SET, 0); }
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode K_WHERE() { return getToken(SQLParser.K_WHERE, 0); }
		public Update_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitUpdate_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Update_stmtContext update_stmt() throws RecognitionException {
		Update_stmtContext _localctx = new Update_stmtContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_update_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			match(K_UPDATE);
			setState(229);
			table_name();
			setState(230);
			match(K_SET);
			setState(231);
			column_name();
			setState(232);
			match(T__4);
			setState(233);
			expr(0);
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(234);
				match(T__2);
				setState(235);
				column_name();
				setState(236);
				match(T__4);
				setState(237);
				expr(0);
				}
				}
				setState(243);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(246);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHERE) {
				{
				setState(244);
				match(K_WHERE);
				setState(245);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_defContext extends ParserRuleContext {
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public Type_nameContext type_name() {
			return getRuleContext(Type_nameContext.class,0);
		}
		public List<Column_constraintContext> column_constraint() {
			return getRuleContexts(Column_constraintContext.class);
		}
		public Column_constraintContext column_constraint(int i) {
			return getRuleContext(Column_constraintContext.class,i);
		}
		public Column_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_def; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitColumn_def(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Column_defContext column_def() throws RecognitionException {
		Column_defContext _localctx = new Column_defContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_column_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(248);
			column_name();
			setState(249);
			type_name();
			setState(253);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==K_NOT || _la==K_PRIMARY) {
				{
				{
				setState(250);
				column_constraint();
				}
				}
				setState(255);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_nameContext extends ParserRuleContext {
		public Type_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_name; }
	 
		public Type_nameContext() { }
		public void copyFrom(Type_nameContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TypeIntContext extends Type_nameContext {
		public TerminalNode T_INT() { return getToken(SQLParser.T_INT, 0); }
		public TypeIntContext(Type_nameContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitTypeInt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TypeFloatContext extends Type_nameContext {
		public TerminalNode T_FLOAT() { return getToken(SQLParser.T_FLOAT, 0); }
		public TypeFloatContext(Type_nameContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitTypeFloat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TypeStringContext extends Type_nameContext {
		public TerminalNode T_STRING() { return getToken(SQLParser.T_STRING, 0); }
		public TerminalNode INTEGER() { return getToken(SQLParser.INTEGER, 0); }
		public TypeStringContext(Type_nameContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitTypeString(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TypeLongContext extends Type_nameContext {
		public TerminalNode T_LONG() { return getToken(SQLParser.T_LONG, 0); }
		public TypeLongContext(Type_nameContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitTypeLong(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TypeDoubleContext extends Type_nameContext {
		public TerminalNode T_DOUBLE() { return getToken(SQLParser.T_DOUBLE, 0); }
		public TypeDoubleContext(Type_nameContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitTypeDouble(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_nameContext type_name() throws RecognitionException {
		Type_nameContext _localctx = new Type_nameContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_type_name);
		try {
			setState(264);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T_INT:
				_localctx = new TypeIntContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(256);
				match(T_INT);
				}
				break;
			case T_LONG:
				_localctx = new TypeLongContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(257);
				match(T_LONG);
				}
				break;
			case T_FLOAT:
				_localctx = new TypeFloatContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(258);
				match(T_FLOAT);
				}
				break;
			case T_DOUBLE:
				_localctx = new TypeDoubleContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(259);
				match(T_DOUBLE);
				}
				break;
			case T_STRING:
				_localctx = new TypeStringContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(260);
				match(T_STRING);
				setState(261);
				match(T__1);
				setState(262);
				match(INTEGER);
				setState(263);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_constraintContext extends ParserRuleContext {
		public Column_constraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_constraint; }
	 
		public Column_constraintContext() { }
		public void copyFrom(Column_constraintContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NotNullConstraintContext extends Column_constraintContext {
		public TerminalNode K_NOT() { return getToken(SQLParser.K_NOT, 0); }
		public TerminalNode K_NULL() { return getToken(SQLParser.K_NULL, 0); }
		public NotNullConstraintContext(Column_constraintContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitNotNullConstraint(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PrimaryKeyConstraintContext extends Column_constraintContext {
		public TerminalNode K_PRIMARY() { return getToken(SQLParser.K_PRIMARY, 0); }
		public TerminalNode K_KEY() { return getToken(SQLParser.K_KEY, 0); }
		public PrimaryKeyConstraintContext(Column_constraintContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitPrimaryKeyConstraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Column_constraintContext column_constraint() throws RecognitionException {
		Column_constraintContext _localctx = new Column_constraintContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_column_constraint);
		try {
			setState(270);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_PRIMARY:
				_localctx = new PrimaryKeyConstraintContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(266);
				match(K_PRIMARY);
				setState(267);
				match(K_KEY);
				}
				break;
			case K_NOT:
				_localctx = new NotNullConstraintContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(268);
				match(K_NOT);
				setState(269);
				match(K_NULL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public Literal_valueContext literal_value() {
			return getRuleContext(Literal_valueContext.class,0);
		}
		public Column_full_nameContext column_full_name() {
			return getRuleContext(Column_full_nameContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 44;
		enterRecursionRule(_localctx, 44, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(273);
				literal_value();
				}
				break;
			case 2:
				{
				setState(274);
				column_full_name();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(282);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(277);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(278);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(279);
					expr(2);
					}
					} 
				}
				setState(284);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Table_constraintContext extends ParserRuleContext {
		public TerminalNode K_PRIMARY() { return getToken(SQLParser.K_PRIMARY, 0); }
		public TerminalNode K_KEY() { return getToken(SQLParser.K_KEY, 0); }
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public Table_constraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_constraint; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitTable_constraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Table_constraintContext table_constraint() throws RecognitionException {
		Table_constraintContext _localctx = new Table_constraintContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_table_constraint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			match(K_PRIMARY);
			setState(286);
			match(K_KEY);
			setState(287);
			match(T__1);
			setState(288);
			column_name();
			setState(289);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Result_columnContext extends ParserRuleContext {
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public Column_full_nameContext column_full_name() {
			return getRuleContext(Column_full_nameContext.class,0);
		}
		public Result_columnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result_column; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitResult_column(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Result_columnContext result_column() throws RecognitionException {
		Result_columnContext _localctx = new Result_columnContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_result_column);
		try {
			setState(297);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(291);
				match(T__10);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(292);
				table_name();
				setState(293);
				match(T__11);
				setState(294);
				match(T__10);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(296);
				column_full_name();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_queryContext extends ParserRuleContext {
		public List<Table_nameContext> table_name() {
			return getRuleContexts(Table_nameContext.class);
		}
		public Table_nameContext table_name(int i) {
			return getRuleContext(Table_nameContext.class,i);
		}
		public TerminalNode K_JOIN() { return getToken(SQLParser.K_JOIN, 0); }
		public TerminalNode K_ON() { return getToken(SQLParser.K_ON, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Table_queryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_query; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitTable_query(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Table_queryContext table_query() throws RecognitionException {
		Table_queryContext _localctx = new Table_queryContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_table_query);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			table_name();
			setState(305);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_JOIN) {
				{
				setState(300);
				match(K_JOIN);
				setState(301);
				table_name();
				setState(302);
				match(K_ON);
				setState(303);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Literal_valueContext extends ParserRuleContext {
		public TerminalNode NUMERIC_LITERAL() { return getToken(SQLParser.NUMERIC_LITERAL, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(SQLParser.STRING_LITERAL, 0); }
		public TerminalNode K_NULL() { return getToken(SQLParser.K_NULL, 0); }
		public Literal_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitLiteral_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Literal_valueContext literal_value() throws RecognitionException {
		Literal_valueContext _localctx = new Literal_valueContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_literal_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(307);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_NULL) | (1L << NUMERIC_LITERAL) | (1L << STRING_LITERAL))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Database_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Database_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_database_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitDatabase_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Database_nameContext database_name() throws RecognitionException {
		Database_nameContext _localctx = new Database_nameContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_database_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(309);
			any_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Table_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitTable_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Table_nameContext table_name() throws RecognitionException {
		Table_nameContext _localctx = new Table_nameContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_table_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(311);
			any_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_full_nameContext extends ParserRuleContext {
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public Column_full_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_full_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitColumn_full_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Column_full_nameContext column_full_name() throws RecognitionException {
		Column_full_nameContext _localctx = new Column_full_nameContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_column_full_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(313);
				table_name();
				setState(314);
				match(T__11);
				}
				break;
			}
			setState(318);
			column_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Column_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitColumn_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Column_nameContext column_name() throws RecognitionException {
		Column_nameContext _localctx = new Column_nameContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_column_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(320);
			any_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Any_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SQLParser.IDENTIFIER, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(SQLParser.STRING_LITERAL, 0); }
		public Any_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_any_name; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLVisitor ) return ((SQLVisitor<? extends T>)visitor).visitAny_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Any_nameContext any_name() throws RecognitionException {
		Any_nameContext _localctx = new Any_nameContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_any_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			_la = _input.LA(1);
			if ( !(_la==IDENTIFIER || _la==STRING_LITERAL) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 22:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3>\u0147\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\3\2\3\2\5\2E\n\2\3\3\3\3\3\3\3\4\7\4K\n\4\f\4\16\4N\13\4\3\4\3\4\6"+
		"\4R\n\4\r\4\16\4S\3\4\7\4W\n\4\f\4\16\4Z\13\4\3\4\7\4]\n\4\f\4\16\4`\13"+
		"\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5o\n\5\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\7\b\u0080\n\b\f"+
		"\b\16\b\u0083\13\b\3\b\3\b\5\b\u0087\n\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3"+
		"\n\3\n\3\n\5\n\u0093\n\n\3\13\3\13\3\13\3\13\5\13\u0099\n\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\7\17\u00ad\n\17\f\17\16\17\u00b0\13\17\3\17\3\17\5\17\u00b4\n\17"+
		"\3\17\3\17\3\17\3\17\7\17\u00ba\n\17\f\17\16\17\u00bd\13\17\3\20\3\20"+
		"\3\20\3\20\7\20\u00c3\n\20\f\20\16\20\u00c6\13\20\3\20\3\20\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\5\22\u00d1\n\22\5\22\u00d3\n\22\3\23\3\23\5"+
		"\23\u00d7\n\23\3\23\3\23\3\23\7\23\u00dc\n\23\f\23\16\23\u00df\13\23\3"+
		"\23\3\23\3\23\3\23\5\23\u00e5\n\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\7\24\u00f2\n\24\f\24\16\24\u00f5\13\24\3\24\3\24"+
		"\5\24\u00f9\n\24\3\25\3\25\3\25\7\25\u00fe\n\25\f\25\16\25\u0101\13\25"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u010b\n\26\3\27\3\27\3\27"+
		"\3\27\5\27\u0111\n\27\3\30\3\30\3\30\5\30\u0116\n\30\3\30\3\30\3\30\7"+
		"\30\u011b\n\30\f\30\16\30\u011e\13\30\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\32\3\32\3\32\3\32\3\32\3\32\5\32\u012c\n\32\3\33\3\33\3\33\3\33\3\33"+
		"\3\33\5\33\u0134\n\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3\37\5\37"+
		"\u013f\n\37\3\37\3\37\3 \3 \3!\3!\3!\2\3.\"\2\4\6\b\n\f\16\20\22\24\26"+
		"\30\32\34\36 \"$&(*,.\60\62\64\668:<>@\2\7\4\2\25\25\35\35\4\2\26\26\36"+
		"\36\3\2\7\f\4\2((9:\4\2\67\67::\2\u0152\2D\3\2\2\2\4F\3\2\2\2\6L\3\2\2"+
		"\2\bn\3\2\2\2\np\3\2\2\2\ft\3\2\2\2\16x\3\2\2\2\20\u008a\3\2\2\2\22\u008d"+
		"\3\2\2\2\24\u0094\3\2\2\2\26\u009c\3\2\2\2\30\u009f\3\2\2\2\32\u00a1\3"+
		"\2\2\2\34\u00a5\3\2\2\2\36\u00be\3\2\2\2 \u00c9\3\2\2\2\"\u00cb\3\2\2"+
		"\2$\u00d4\3\2\2\2&\u00e6\3\2\2\2(\u00fa\3\2\2\2*\u010a\3\2\2\2,\u0110"+
		"\3\2\2\2.\u0115\3\2\2\2\60\u011f\3\2\2\2\62\u012b\3\2\2\2\64\u012d\3\2"+
		"\2\2\66\u0135\3\2\2\28\u0137\3\2\2\2:\u0139\3\2\2\2<\u013e\3\2\2\2>\u0142"+
		"\3\2\2\2@\u0144\3\2\2\2BE\5\6\4\2CE\5\4\3\2DB\3\2\2\2DC\3\2\2\2E\3\3\2"+
		"\2\2FG\7>\2\2GH\b\3\1\2H\5\3\2\2\2IK\7\3\2\2JI\3\2\2\2KN\3\2\2\2LJ\3\2"+
		"\2\2LM\3\2\2\2MO\3\2\2\2NL\3\2\2\2OX\5\b\5\2PR\7\3\2\2QP\3\2\2\2RS\3\2"+
		"\2\2SQ\3\2\2\2ST\3\2\2\2TU\3\2\2\2UW\5\b\5\2VQ\3\2\2\2WZ\3\2\2\2XV\3\2"+
		"\2\2XY\3\2\2\2Y^\3\2\2\2ZX\3\2\2\2[]\7\3\2\2\\[\3\2\2\2]`\3\2\2\2^\\\3"+
		"\2\2\2^_\3\2\2\2_\7\3\2\2\2`^\3\2\2\2ao\5\16\b\2bo\5\n\6\2co\5\f\7\2d"+
		"o\5\22\n\2eo\5\24\13\2fo\5\34\17\2go\5 \21\2ho\5\"\22\2io\5\20\t\2jo\5"+
		"\26\f\2ko\5\32\16\2lo\5\30\r\2mo\5&\24\2na\3\2\2\2nb\3\2\2\2nc\3\2\2\2"+
		"nd\3\2\2\2ne\3\2\2\2nf\3\2\2\2ng\3\2\2\2nh\3\2\2\2ni\3\2\2\2nj\3\2\2\2"+
		"nk\3\2\2\2nl\3\2\2\2nm\3\2\2\2o\t\3\2\2\2pq\7\31\2\2qr\7\32\2\2rs\58\35"+
		"\2s\13\3\2\2\2tu\7\37\2\2uv\7\32\2\2vw\58\35\2w\r\3\2\2\2xy\7\31\2\2y"+
		"z\7\61\2\2z{\5:\36\2{|\7\4\2\2|\u0081\5(\25\2}~\7\5\2\2~\u0080\5(\25\2"+
		"\177}\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3\2\2"+
		"\2\u0082\u0086\3\2\2\2\u0083\u0081\3\2\2\2\u0084\u0085\7\5\2\2\u0085\u0087"+
		"\5\60\31\2\u0086\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0088\3\2\2\2"+
		"\u0088\u0089\7\6\2\2\u0089\17\3\2\2\2\u008a\u008b\7\64\2\2\u008b\u008c"+
		"\58\35\2\u008c\21\3\2\2\2\u008d\u008e\7\34\2\2\u008e\u008f\7!\2\2\u008f"+
		"\u0092\5:\36\2\u0090\u0091\7\66\2\2\u0091\u0093\5.\30\2\u0092\u0090\3"+
		"\2\2\2\u0092\u0093\3\2\2\2\u0093\23\3\2\2\2\u0094\u0095\7\37\2\2\u0095"+
		"\u0098\7\61\2\2\u0096\u0097\7\"\2\2\u0097\u0099\7 \2\2\u0098\u0096\3\2"+
		"\2\2\u0098\u0099\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009b\5:\36\2\u009b"+
		"\25\3\2\2\2\u009c\u009d\7\60\2\2\u009d\u009e\7\33\2\2\u009e\27\3\2\2\2"+
		"\u009f\u00a0\7,\2\2\u00a0\31\3\2\2\2\u00a1\u00a2\7\60\2\2\u00a2\u00a3"+
		"\7\62\2\2\u00a3\u00a4\58\35\2\u00a4\33\3\2\2\2\u00a5\u00a6\7#\2\2\u00a6"+
		"\u00a7\7$\2\2\u00a7\u00b3\5:\36\2\u00a8\u00a9\7\4\2\2\u00a9\u00ae\5> "+
		"\2\u00aa\u00ab\7\5\2\2\u00ab\u00ad\5> \2\u00ac\u00aa\3\2\2\2\u00ad\u00b0"+
		"\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b1\3\2\2\2\u00b0"+
		"\u00ae\3\2\2\2\u00b1\u00b2\7\6\2\2\u00b2\u00b4\3\2\2\2\u00b3\u00a8\3\2"+
		"\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b6\7\65\2\2\u00b6"+
		"\u00bb\5\36\20\2\u00b7\u00b8\7\5\2\2\u00b8\u00ba\5\36\20\2\u00b9\u00b7"+
		"\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc"+
		"\35\3\2\2\2\u00bd\u00bb\3\2\2\2\u00be\u00bf\7\4\2\2\u00bf\u00c4\5.\30"+
		"\2\u00c0\u00c1\7\5\2\2\u00c1\u00c3\5.\30\2\u00c2\u00c0\3\2\2\2\u00c3\u00c6"+
		"\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c7\3\2\2\2\u00c6"+
		"\u00c4\3\2\2\2\u00c7\u00c8\7\6\2\2\u00c8\37\3\2\2\2\u00c9\u00ca\7-\2\2"+
		"\u00ca!\3\2\2\2\u00cb\u00d2\5$\23\2\u00cc\u00cd\7*\2\2\u00cd\u00ce\7\27"+
		"\2\2\u00ce\u00d0\5> \2\u00cf\u00d1\t\2\2\2\u00d0\u00cf\3\2\2\2\u00d0\u00d1"+
		"\3\2\2\2\u00d1\u00d3\3\2\2\2\u00d2\u00cc\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3"+
		"#\3\2\2\2\u00d4\u00d6\7.\2\2\u00d5\u00d7\t\3\2\2\u00d6\u00d5\3\2\2\2\u00d6"+
		"\u00d7\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00dd\5\62\32\2\u00d9\u00da\7"+
		"\5\2\2\u00da\u00dc\5\62\32\2\u00db\u00d9\3\2\2\2\u00dc\u00df\3\2\2\2\u00dd"+
		"\u00db\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00e0\3\2\2\2\u00df\u00dd\3\2"+
		"\2\2\u00e0\u00e1\7!\2\2\u00e1\u00e4\5\64\33\2\u00e2\u00e3\7\66\2\2\u00e3"+
		"\u00e5\5.\30\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5%\3\2\2\2"+
		"\u00e6\u00e7\7\63\2\2\u00e7\u00e8\5:\36\2\u00e8\u00e9\7/\2\2\u00e9\u00ea"+
		"\5> \2\u00ea\u00eb\7\7\2\2\u00eb\u00f3\5.\30\2\u00ec\u00ed\7\5\2\2\u00ed"+
		"\u00ee\5> \2\u00ee\u00ef\7\7\2\2\u00ef\u00f0\5.\30\2\u00f0\u00f2\3\2\2"+
		"\2\u00f1\u00ec\3\2\2\2\u00f2\u00f5\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f3\u00f4"+
		"\3\2\2\2\u00f4\u00f8\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f6\u00f7\7\66\2\2"+
		"\u00f7\u00f9\5.\30\2\u00f8\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\'\3"+
		"\2\2\2\u00fa\u00fb\5> \2\u00fb\u00ff\5*\26\2\u00fc\u00fe\5,\27\2\u00fd"+
		"\u00fc\3\2\2\2\u00fe\u0101\3\2\2\2\u00ff\u00fd\3\2\2\2\u00ff\u0100\3\2"+
		"\2\2\u0100)\3\2\2\2\u0101\u00ff\3\2\2\2\u0102\u010b\7\17\2\2\u0103\u010b"+
		"\7\20\2\2\u0104\u010b\7\21\2\2\u0105\u010b\7\22\2\2\u0106\u0107\7\23\2"+
		"\2\u0107\u0108\7\4\2\2\u0108\u0109\78\2\2\u0109\u010b\7\6\2\2\u010a\u0102"+
		"\3\2\2\2\u010a\u0103\3\2\2\2\u010a\u0104\3\2\2\2\u010a\u0105\3\2\2\2\u010a"+
		"\u0106\3\2\2\2\u010b+\3\2\2\2\u010c\u010d\7+\2\2\u010d\u0111\7&\2\2\u010e"+
		"\u010f\7\'\2\2\u010f\u0111\7(\2\2\u0110\u010c\3\2\2\2\u0110\u010e\3\2"+
		"\2\2\u0111-\3\2\2\2\u0112\u0113\b\30\1\2\u0113\u0116\5\66\34\2\u0114\u0116"+
		"\5<\37\2\u0115\u0112\3\2\2\2\u0115\u0114\3\2\2\2\u0116\u011c\3\2\2\2\u0117"+
		"\u0118\f\3\2\2\u0118\u0119\t\4\2\2\u0119\u011b\5.\30\4\u011a\u0117\3\2"+
		"\2\2\u011b\u011e\3\2\2\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d"+
		"/\3\2\2\2\u011e\u011c\3\2\2\2\u011f\u0120\7+\2\2\u0120\u0121\7&\2\2\u0121"+
		"\u0122\7\4\2\2\u0122\u0123\5> \2\u0123\u0124\7\6\2\2\u0124\61\3\2\2\2"+
		"\u0125\u012c\7\r\2\2\u0126\u0127\5:\36\2\u0127\u0128\7\16\2\2\u0128\u0129"+
		"\7\r\2\2\u0129\u012c\3\2\2\2\u012a\u012c\5<\37\2\u012b\u0125\3\2\2\2\u012b"+
		"\u0126\3\2\2\2\u012b\u012a\3\2\2\2\u012c\63\3\2\2\2\u012d\u0133\5:\36"+
		"\2\u012e\u012f\7%\2\2\u012f\u0130\5:\36\2\u0130\u0131\7)\2\2\u0131\u0132"+
		"\5.\30\2\u0132\u0134\3\2\2\2\u0133\u012e\3\2\2\2\u0133\u0134\3\2\2\2\u0134"+
		"\65\3\2\2\2\u0135\u0136\t\5\2\2\u0136\67\3\2\2\2\u0137\u0138\5@!\2\u0138"+
		"9\3\2\2\2\u0139\u013a\5@!\2\u013a;\3\2\2\2\u013b\u013c\5:\36\2\u013c\u013d"+
		"\7\16\2\2\u013d\u013f\3\2\2\2\u013e\u013b\3\2\2\2\u013e\u013f\3\2\2\2"+
		"\u013f\u0140\3\2\2\2\u0140\u0141\5> \2\u0141=\3\2\2\2\u0142\u0143\5@!"+
		"\2\u0143?\3\2\2\2\u0144\u0145\t\6\2\2\u0145A\3\2\2\2\37DLSX^n\u0081\u0086"+
		"\u0092\u0098\u00ae\u00b3\u00bb\u00c4\u00d0\u00d2\u00d6\u00dd\u00e4\u00f3"+
		"\u00f8\u00ff\u010a\u0110\u0115\u011c\u012b\u0133\u013e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
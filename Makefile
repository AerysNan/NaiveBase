all: parser

TARGET := SQL.g4
CONFIG := -no-listener -visitor -package parser

parser:
ifeq ($(OS), Windows_NT)
	cd src/parser/ ;\
	cmd //C antlr $(TARGET) $(CONFIG)
else
	cd src/parser/ ;\
	$(antlr) $(TARGET) $(CONFIG)
endif

proto:
	cd src/proto/ ;\
	protoc sql.proto --java_out=../

clean:
	rm -rf meta/
	rm -rf data/
	rm -rf databases.dat
package schema;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;

public class Database {
  String name;
  HashMap<String, Table> tables;

  public Database(String name) throws Exception {
    this.name = name;
    this.tables = new HashMap<>();
    recoverDatabase();
  }

  public Table createTable(String name, Column[] columns) throws Exception {
    Table table = new Table(this.name, name, columns);
    tables.put(name, table);
    return table;
  }

  public void persistDatabase() throws IOException {
    File path = new File(Manager.metadataPath);
    if (!path.exists()) {
      return;
    }
    for (Table t : tables.values()) {
      persistTable(t);
    }
  }

  public void persistTable(Table table) throws IOException {
    File file = new File(Manager.metadataPath + this.name + "_" + table.name + ".dat");
    FileWriter fileWriter = new FileWriter(file, false);
    for (Column c : table.columns)
      fileWriter.write(c.toString() + "\n");
    fileWriter.close();
  }

  public void recoverDatabase() throws Exception {
    File path = new File(Manager.metadataPath);
    if (!path.exists()) {
      return;
    }
    File[] files = path.listFiles();
    for (File f : files) {
      // TODO reduce time complexity
      if (!f.getName().startsWith(this.name))
        continue;
      recoverTable(f);
    }
  }

  public void recoverTable(File f) throws Exception {
    FileReader fileReader = new FileReader(f);
    BufferedReader reader = new BufferedReader(fileReader);
    String tableName = f.getName().split("_")[1].replace(".dat", "");
    ArrayList<Column> colArrayList = new ArrayList<>();
    String strColumn = null;
    while ((strColumn = reader.readLine()) != null) {
      String[] tmpColumn = strColumn.split(",");
      Type type = Type.INT;
      switch (tmpColumn[1]) {
        case "INT":
          type = Type.INT;
          break;
        case "LONG":
          type = Type.LONG;
          break;
        case "FLOAT":
          type = Type.FLOAT;
          break;
        case "DOUBLE":
          type = Type.DOUBLE;
          break;
        case "STRING":
          type = Type.STRING;
          break;
      }
      Column tmpCol = new Column(tmpColumn[0], type, Boolean.parseBoolean(tmpColumn[2]));
      colArrayList.add(tmpCol);
    }
    reader.close();
    Column colList[] = colArrayList.toArray(new Column[colArrayList.size()]);
    Table table = new Table(this.name, tableName, colList);
    tables.put(tableName, table);
  }

  public void quit() throws IOException {
    persistDatabase();
    for (Table t : tables.values())
      t.commit();
  }
}
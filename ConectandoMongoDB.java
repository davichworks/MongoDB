package com.mycompany.conexionmongodb;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.bson.types.ObjectId;

public class ConectandoMongoDB {
    
    // Jtable que se mostrara en pantalla.
    
    private static JTable table;

    public static void main(String[] args) {
        
        //metodo Runnable para correr la app
        
            Runnable runnable;
            runnable = new Runnable(){
            public void run(){               
                table = new JTable(){
                    @Override
                    public Dimension getPreferredScrollableViewportSize() {
                        return new Dimension(300, 150);
                    }
                };
                
                // Creamos los componentes de nuestro frame y los añadimos tanto al panel como a los eventos
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(Color.CYAN);
                table.setBackground(Color.PINK);
                JButton mostrar = new JButton("Ver datos");
                JButton añadir = new JButton("Añadir");
                JButton eliminar = new JButton("Eliminar");
                JButton modificar = new JButton("Modificar");
                añadir.addActionListener(añadirlis);
                eliminar.addActionListener(borrarlis);
                modificar.addActionListener(modificarlis);
                mostrar.addActionListener(ConexionMostrartabla);
                panel.add(new JScrollPane(table));
                panel.add(modificar,BorderLayout.EAST);
                panel.add(mostrar, BorderLayout.WEST);
                panel.add(eliminar,BorderLayout.PAGE_END);
                panel.add(añadir,BorderLayout.PAGE_START);
                JOptionPane.showMessageDialog(null, panel);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }
    
    // Evento que carga la colleccion de la base de datos y carga los datos en una tabla

    static ActionListener ConexionMostrartabla = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MongoClient mongo = null;
            DBCursor cursor = null;
            
            try{
                // conexion a MongoDB "localhost"
                
            mongo = new MongoClient( "localhost" , 27017 );
            
            //Almacenamos en la variable la base de datos  
            
            DB db = mongo.getDB( "Datos" );
            
            // Almacenamos en la variable la coleccion
            
            DBCollection coll = db.getCollection("Criminales");
            cursor = coll.find();
            String[] columnNames = {"id", "Nombre", "Apellido","Delito"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            
            // Metodo para crear una tabla según los datos de nuestra coleccion
            
            while(cursor.hasNext()) {
                DBObject obj = cursor.next();
                 ObjectId id = (ObjectId)obj.get("_id");
                String nombre = (String)obj.get("Nombre");
                String apellido = (String)obj.get("Apellido");
                String delito = (String)obj.get("Delito");
               
                model.addRow(new Object[] { id, nombre, apellido,delito });
            }
            table.setModel(model);
            cursor.close();
            mongo.close();
            } catch (Exception ex){
              
                Logger.getLogger(ConectandoMongoDB.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
            if (cursor!= null) {
                cursor.close();
            } 
            if (mongo != null) {
                mongo.close();
            }
        }
       }
        
    };
    
    //Evento para obtener los datos del nuevo documento a añadir
    
    static ActionListener añadirlis = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
           MongoClient mongo = null;
           mongo = new MongoClient( "localhost" , 27017 );
           DB db = mongo.getDB( "Datos" );
           DBCollection coll = db.getCollection("Criminales");
           
           
            String respnom = (String) JOptionPane.showInputDialog(null,"Escriba el nombre");
            String respape = (String) JOptionPane.showInputDialog(null,"Escriba el apellido");
            String respdel = (String) JOptionPane.showInputDialog(null,"Escriba el delito");
            
            insertarDelincuente(db,coll,respnom,respape,respdel);
            
            ConectandoMongoDB.ConexionMostrartabla.actionPerformed(e);
            
        }
         
        // Metodo para añadir un documento a la coleccion de una base de datos.
        public void insertarDelincuente(DB db, DBCollection coleccion, String nombre, String apellido,String delito){
        
        BasicDBObject documento = new BasicDBObject();
        documento.put("_id",documento.getObjectId(nombre));
        documento.put("Nombre",nombre);
        documento.put("Apellido",apellido);
        documento.put("Delito",delito);
        
        coleccion.insert(documento);
    }
        
    };
    
    //Evento para obtener el documento que se quiere borrar
    static ActionListener borrarlis = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
           MongoClient mongo = null;
           mongo = new MongoClient( "localhost" , 27017 );
           DB db = mongo.getDB( "Datos" );
           DBCollection coll = db.getCollection("Criminales");
          int filaSeleccionada = table.getSelectedRow();
            
        
            
            ObjectId id1 = (ObjectId)table.getValueAt(filaSeleccionada, 0);
            
       
           eliminarDelincuente(db,coll,id1); 
           ConectandoMongoDB.ConexionMostrartabla.actionPerformed(e);

        }
        
        //Metodo para eliminar un documento de la coleccion
        
       public void eliminarDelincuente(DB db, DBCollection coleccion, ObjectId id){
        
        BasicDBObject filtro = new BasicDBObject();
        
        filtro.put("_id",id);
        coleccion.remove(filtro);
        
    }
        
        
    };
    
        //Evento para obtener el dato a modificar y el nuevo a sustituir
    static ActionListener modificarlis = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MongoClient mongo = null;
           mongo = new MongoClient( "localhost" , 27017 );
           DB db = mongo.getDB( "Datos" );
           DBCollection coll = db.getCollection("Criminales");
           int filaseleccionada1 = table.getSelectedRow();
             ObjectId id2 = (ObjectId)table.getValueAt(filaseleccionada1, 0);
             String seleccion2 = null;
             
             int columnaseleccionada1 = table.getSelectedColumn();
             if(columnaseleccionada1==0){
                 seleccion2=null;
                 JOptionPane.showMessageDialog(table, "No se puede modificar la ID");
             }else if(columnaseleccionada1==1){
                 seleccion2="Nombre";
             }else if(columnaseleccionada1==2){
                 seleccion2="Apellido";
             }else if(columnaseleccionada1==3){
                 seleccion2="Delito";
             }else {
                 JOptionPane.showMessageDialog(table,"Error al seleccionar");
             }
             
            if(seleccion2 != null){
             String respuesta2 = (String)JOptionPane.showInputDialog("Escriba el cambio");
              modificarDelincuente(db,coll,id2,seleccion2,respuesta2);
            }
             
           
           ConectandoMongoDB.ConexionMostrartabla.actionPerformed(e);
        }
        
        //Metodo que modifica un documento en la coleccion
        
         public void modificarDelincuente(DB db, DBCollection coleccion,  ObjectId id, String seleccion2,String respuesta2){
        
          
         BasicDBObject oquery = new BasicDBObject();
         oquery.put("_id", id);
         
         BasicDBObject doc = new BasicDBObject();
         BasicDBObject op = new BasicDBObject();
         op.put(seleccion2,respuesta2);
         doc.put("$set",op);
         coleccion.update(oquery,doc);
            
         
    }
        
    };
    
    
    
    
}

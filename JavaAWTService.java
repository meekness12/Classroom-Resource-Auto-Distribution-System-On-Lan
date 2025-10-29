import java.awt.*;
import java.awt.event.*;

public class JavaAWTService extends Frame implements ActionListener {

    // Declare components
    Label labelValue1, labelValue2, labelResult;
    TextField txtValue1, txtValue2;
    Button btnAdd;

    // Constructor
    JavaAWTService() {
Frame f = new Frame();
        // Initialize labels
        labelValue1 = new Label("Value 1:");
        labelValue2 = new Label("Value 2:");
        labelResult = new Label("Result: ");

        // Initialize text fields
        txtValue1 = new TextField();
        txtValue2 = new TextField();

        // Initialize button
        btnAdd = new Button("Add");

        // Set component positions
        labelValue1.setBounds(50, 60, 60, 25);
        txtValue1.setBounds(120, 60, 100, 25);

        labelValue2.setBounds(50, 100, 60, 25);
        txtValue2.setBounds(120, 100, 100, 25);

        btnAdd.setBounds(50, 140, 80, 30);
        labelResult.setBounds(50, 180, 200, 25);

        // Add components to the frame
        add(labelValue1);
        add(txtValue1);
        add(labelValue2);
        add(txtValue2);
        add(btnAdd);
        add(labelResult);

        // Add action listener
        btnAdd.addActionListener(this);

        // Frame settings
        setSize(300, 250);
        setLayout(null);
        setTitle("addition AWT Adder");

        // Close window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

       setVisible(true);

    }

    // Button action event
    public void actionPerformed(ActionEvent e) {
        try {
            int num1 = Integer.parseInt(txtValue1.getText());
            int num2 = Integer.parseInt(txtValue2.getText());
            int sum = num1 + num2;
            labelResult.setText("Result: " + sum);
        } catch (NumberFormatException ex) {
            labelResult.setText("Please enter valid numbers!");
        }
    }

    // Main method
    public static void main(String[] args) {
        new JavaAWTService();
    }
}

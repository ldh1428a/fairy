package fairy.core.command;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import fairy.core.net.communicator.Linker;
import fairy.core.utils.Debugger;
import fairy.valueobject.managers.transaction.StatusTransaction;
import fairy.valueobject.managers.transaction.Transaction;

public class CommandLayout extends JFrame {
	private JTextField textField = null;
	private JTextArea textArea = null;
	
	private static CommandLayout instance = null;
	
	private CommandLayout()
	{
		super("Fairy - Command Line Interface [test.v]");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setResizable(false);
		getContentPane().setLayout(null);
		
		setSize(947,681);
		
		textField = new JTextField();
		textField.setBounds(78, 606, 841, 26);
		getContentPane().add(textField);
		textField.setColumns(10);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(ExcuteCommand(textField.getText())) {
					textField.setBackground(new Color(159, 244, 180));
					textField.setText("");
				}else {
					textField.setBackground(new Color(255, 149, 149));
					textField.setText("");
				}
			}
		});
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(12, 10, 907, 586);
		getContentPane().add(textArea);
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		textArea.setBorder(BorderFactory.createCompoundBorder(border, 
		            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		textField.setBorder(BorderFactory.createCompoundBorder(border, 
	            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		JLabel lblCommand = new JLabel("Command");
		lblCommand.setFont(new Font("����", Font.PLAIN, 12));
		lblCommand.setBounds(12, 611, 73, 17);
		getContentPane().add(lblCommand);
	
		setVisible(true);
	}
	
	private boolean ExcuteCommand(String command)
	{
		switch(command)
		{
		case CommandList.EXAMPLE_TRANSACTION:
			Transaction tx = new StatusTransaction();
			return Linker.getInstance().broadcastingTransactrion(tx);
			default:
				return false;
		}
	}
	
	public boolean addMessage(String message)
	{
		try {
			textArea.append(message);
			return true;
		}catch(Exception e) {
			Debugger.Log(this, e);
			return false;
		}
	}
	
	public static CommandLayout getInstance() {
		if(instance == null) {
			instance = new CommandLayout();
		}
		return instance;
	}
}
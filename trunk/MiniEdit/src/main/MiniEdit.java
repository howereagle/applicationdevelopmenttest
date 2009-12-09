package main;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// TODO: implementar outras funcionalidades
// TODO: menu clique do mouse (cap05/swingPopup)
// TODO: descobrir como exportar .jar com as imagens 
public class MiniEdit extends JFrame {

	private static String sAjuda[] = { "Ajuda", "help16.gif", "A", null, null, null, "Sobre ...", "about16.gif", "S" };
	private static String sArquivo[] = { "Novo", "new16.gif", "N", "Abrir ...", "open16.gif", "A", "Salvar", "save16.gif", "S", "Salvar como ...", "saveas16.gif", "c", null, null, null, "Imprimir ...", "print16.gif", "I", null, null, null, "Sair", "blank16.gif", "r" };
	private static String sEditar[] = { "Recortar", "cut16.gif", "R", "Copiar", "copy16.gif", "C", "Colar", "paste16.gif", "o", null, null, null, "Excluir", "delete16.gif", "x", "Selecionar tudo", "blank16.gif", "t", null, null, null };
	private static final long serialVersionUID = 1L;

	private JTextArea taEditor;

	public MiniEdit() {
		super("MiniEdit");

		JMenuBar mb = new JMenuBar();
		MenuHandler mh = new MenuHandler();
		mb.add(criaMenu("Arquivo", 'A', sArquivo, mh));
		mb.add(criaMenu("Editar", 'E', sEditar, mh));
		mb.add(criaMenu("Ajuda", 'u', sAjuda, mh));
		setJMenuBar(mb);
		JMenu menu = mb.getMenu(1);
		JMenuItem mi = menu.add(new JCheckBoxMenuItem("Quebra de Linha"));
		mi.addActionListener(mh);

		JScrollPane sp = new JScrollPane(taEditor = new JTextArea());
		sp.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
		getContentPane().add(sp);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 400);
	}

	public static void main(String args[]) {
		new MiniEdit().setVisible(true);
	}

	private JMenu criaMenu(String nome, int acc, String itens[], ActionListener al) {
		JMenu menu = new JMenu(nome);
		menu.setMnemonic(acc);
		JMenuItem mi;
		for (int i = 0; i < itens.length; i += 3) {
			if (itens[i] != null) {
				if (itens[i + 1] != null) {
					ImageIcon icon = new ImageIcon("img/" + itens[i + 1]);
					mi = new JMenuItem(itens[i], icon);
				} else {
					mi = new JMenuItem(itens[i]);
				}
				if (itens[i + 2] != null) {
					mi.setMnemonic(itens[i + 2].charAt(0));
				}
				mi.addActionListener(al);
				menu.add(mi);
			} else {
				menu.addSeparator();
			}
		}
		return menu;
	}

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private InputStream convertStringToStream(String text) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return is;
	}

	private class MenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String acao = ((JMenuItem) e.getSource()).getText();
			// Abrir
			if (acao.equals(sArquivo[1 * 3])) {
				JFileChooser openDialog = new JFileChooser();
				int openResult = openDialog.showOpenDialog(MiniEdit.this);
				if (openResult == JFileChooser.APPROVE_OPTION) {
					File choosenFile = openDialog.getSelectedFile();
					// converte arquivo em reader
					BufferedReader br = null;
					try {
						br = new BufferedReader(new FileReader(choosenFile));
					} catch (FileNotFoundException ex) {
						ex.printStackTrace();
					}
					// converte reader em string
					taEditor.setText("");
					try {
						String line;
						while ((line = br.readLine()) != null) {
							taEditor.append(line + "\n");
						}
						br.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			// Salvar como...
			// TODO: dialogo de arquivo existente
			else if (acao.equals(sArquivo[3 * 3])) {
				JFileChooser saveAsDialog = new JFileChooser();
				int saveAsResult = saveAsDialog.showSaveDialog(MiniEdit.this);
				if (saveAsResult == JFileChooser.APPROVE_OPTION) {
					File choosenFile = saveAsDialog.getSelectedFile();
					// abre arquivo de saida
					BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(new FileWriter(choosenFile));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					// salva string no arquivo
					InputStream is = convertStringToStream(taEditor.getText());
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					try {
						String line;
						while ((line = br.readLine()) != null) {
							bw.write(line + "\r\n");
						}
						bw.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			// Sair
			else if (acao.equals(sArquivo[7 * 3])) {
				System.exit(0);
			}
			// Resto
			else {
				taEditor.append(acao + "\n");
			}
		}
	}

}

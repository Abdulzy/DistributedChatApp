package View;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.util.Comparator;

import javax.swing.ImageIcon;

public class Manager {
	public static Manager m =new Manager();
	public static String path = new File("").getAbsolutePath();
	//new File("").getAbsolutePath();

	public Manager() {
		super();
	}
	
	public static Image getImg(String sh) {
		try {
			return new ImageIcon(m.getClass().getResource(sh)).getImage();
//			return new ImageIcon("src\\" + sh).getImage();
		} catch (Exception e) {
			System.out.println("!! Error : image {\"" + sh + "\"} not found :: " + e.getMessage());
		}
		return null;
	}

	public static ImageIcon getImg(String sh, int width, int height) {
		try {
			return new ImageIcon(getImg(sh).getScaledInstance(width, height, Image.SCALE_SMOOTH));
		} catch (Exception e) {
			System.out.println("!! Error : image {\"" + sh + "\"} not found :: " + e.getMessage());
			return null;
		}
	}


	public static void readFile(String p,ChatPan chatPan) {
		try {
			Path pt = Paths.get(path + "\\" + p + ".txt");
			try (BufferedReader br = Files.newBufferedReader(pt)) {
				String ligne = null;
				while ((ligne = br.readLine()) != null) {
					int index = ligne.indexOf("]:[");
					String s1 = ligne.substring(0, index);
					String s2 = ligne.substring(index + 3);
					
					if(s1.equals("SM")) 
						chatPan.sendMessage(s2, true);
					else if(s1.equals("RM"))
						chatPan.sendMessage(s2, false);
					else if(s1.equals("SF"))
						chatPan.sendFile(new File(s2), true);
					else if(s1.equals("RF"))
						chatPan.sendFile(new File(s2), false);
				}
			}
		} catch (Exception e) {
			System.out.println("!! Error :" + e);
		}
	}

	public static void readFile(String p, CommunityChat comPan) {
		try {
			Path pt = Paths.get(path + "\\" + p + ".txt");
			try (BufferedReader br = Files.newBufferedReader(pt)) {
				String ligne = null;
				while ((ligne = br.readLine()) != null) {
					int index = ligne.indexOf("]:[");
					String s1 = ligne.substring(0, index);
					String s2 = ligne.substring(index + 3);

					if(s1.equals("SM"))
						comPan.sendMessage(s2, true);
					else if(s1.equals("RM"))
						comPan.sendMessage(s2, false);
					else if(s1.equals("SF"))
						comPan.sendFile(new File(s2), true);
					else if(s1.equals("RF"))
						comPan.sendFile(new File(s2), false);
				}
			}
		} catch (Exception e) {
			System.out.println("!! Error :" + e);
		}
	}

	public static void writeFtile(String p, String contenu) {
		try {
			Charset utf8 = Charset.forName("UTF-8");
			Path pt = Paths.get(path + "\\" + p + ".txt");
			try (BufferedWriter bw = Files.newBufferedWriter(pt, utf8, StandardOpenOption.APPEND)) {
				if (contenu != null && !contenu.equals(""))
					bw.write(contenu + "\n");
			} catch (Exception e) {
				BufferedWriter bw = Files.newBufferedWriter(pt, utf8, StandardOpenOption.CREATE);
				if (contenu != null && !contenu.equals(""))
					bw.write(contenu + "\n");
			}
		} catch (Exception e) {
			System.out.println("!! Error :" + e);

		}
	}
	
	public static void writeMsg(int i,String p, String msg) {
		String s = "";
		switch (i) {
		case 1:
			s="SM";
			break;
		case 2:
			s="RM";
			break;
		case 3:
			s="SF";
			break;
		case 4:
			s="RF";
			break;
		default:
			break;
		}
		s+="]:[";
			
		writeFtile(p, s+msg);
	}

	public static void Mkdir(String dir) {
		File theDir = new File(path + "/" + dir);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			try {
				theDir.mkdir();
			} catch (SecurityException se) {
				se.getStackTrace();
			}
		}
	}

}

package xmlfileanalysis;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MainXml {

	//F_(FILE_ID)_TB.xml 전역변수
	static Document fDoc;

	public static void main(String[] args) {

		long s = System.currentTimeMillis();

		//시작함수
		start();

		long e = System.currentTimeMillis();

		System.out.println("실행 시간 : " + (e - s) / 1000.0);
		System.out.println("사용한 메모리 : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}

	// 시작
	private static void start() {

		File file;
		DocumentBuilderFactory docBuildFact;
		DocumentBuilder docBuild;
		Document tBaseDoc;

		try {
			//T_BASEFILE_TB.xml 
			file = new File("C:\\03과제\\99.과제첨부파일\\1.XML 파일 분석\\T_BASEFILE_TB.xml");
			docBuildFact = DocumentBuilderFactory.newInstance();
			docBuild = docBuildFact.newDocumentBuilder();
			tBaseDoc = docBuild.parse(file);
			tBaseDoc.getDocumentElement().normalize();

			Element root = tBaseDoc.getDocumentElement();
			NodeList rows = root.getElementsByTagName("ROWS");
			Element rowsElement = (Element) rows.item(0);
			NodeList row = rowsElement.getElementsByTagName("ROW");

			for (int i = 0; i < row.getLength(); i++) {
				Element rowElement = (Element) row.item(i);

				//FILE_ID 추출
				try {
					NodeList fileIdList = rowElement.getElementsByTagName("FILE_ID");
					Element fileIdElement = (Element) fileIdList.item(0);
					Node fileIdNode = fileIdElement.getFirstChild();

					String file_id = fileIdNode.getNodeValue();

					//COMMENT에 LICENSE_ID값 넣는 함수
					setLicenseId(file_id);

				} catch (NullPointerException e) {
					// TODO: handle exception
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//COMMENT에 LICENSE_ID값 넣는 함수
	private static void setLicenseId(String file_id) {
		int similarRateNum;
		File file;
		DocumentBuilderFactory docBuildFact;
		DocumentBuilder docBuild;
		boolean flag = false;//조건에 맞을때 true
		try {
			//F_(FILE_ID)_TB.xml
			file = new File("C:\\03과제\\99.과제첨부파일\\1.XML 파일 분석\\F_" + file_id + "_TB.xml");
			docBuildFact = DocumentBuilderFactory.newInstance();
			docBuild = docBuildFact.newDocumentBuilder();
			fDoc = docBuild.parse(file);
			fDoc.getDocumentElement().normalize();

			Element root = fDoc.getDocumentElement();
			NodeList rows = root.getElementsByTagName("ROWS");
			Element rowsElement = (Element) rows.item(0);
			NodeList row = rowsElement.getElementsByTagName("ROW");

			//F_(FILE_ID)_TB.xml의 row수만큼 탐색
			for (int i = 0; i < row.getLength(); i++) {
				Element rowElement = (Element) row.item(i);
				try {
					// ROWID
					NodeList rowId = rowElement.getElementsByTagName("ROWID");
					Element rowIdElement = (Element) rowId.item(0);
					//Node rowIdNode = rowIdElement.getFirstChild();

					// SIMIALR_RATE
					NodeList similarRate = rowElement.getElementsByTagName("SIMILAR_RATE");
					Element similarRateElement = (Element) similarRate.item(0);
					Node similarRateNode = similarRateElement.getFirstChild();

					// F_PID
					NodeList pId = rowElement.getElementsByTagName("P_ID");
					Element pIdElement = (Element) pId.item(0);
					Node pIdNode = pIdElement.getFirstChild();

					similarRateNum = Integer.parseInt(similarRateNode.getNodeValue());
					if ((similarRateNum / 100) > 50) {// 2) SIMILAR_RATE/100 값이 50보다 큰 Row 검색

						//P_ID로 P_(FILE_ID)_TB.xml에 맞는 P_ID의 LICENSC_ID값을 가져오는 함수
						String licenseId = getLicenseId(file_id, pIdNode.getNodeValue());

						// COMMENT
						NodeList comment = rowElement.getElementsByTagName("COMMENT");
						//COMMENT내용변경
						comment.item(0).setTextContent(licenseId);
						//Node commentNode = comment.item(0).getFirstChild();
						
						flag = true;
									
					}
				} catch (NullPointerException e) {
					// TODO: handle exception
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//T_(FILE_ID)_TB.xml 생성
			if (flag)
				makeXml(file_id);

		} catch (NullPointerException e) {
			//e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//P_ID로 P_(FILE_ID)_TB.xml에 맞는 P_ID의 LICENSC_ID값을 가져오는 함수
	private static String getLicenseId(String file_id, String p_id) {

		String license_id = "";
		File file;
		DocumentBuilderFactory docBuildFact;
		DocumentBuilder docBuild;
		Document Doc;
		try {
			file = new File("C:\\03과제\\99.과제첨부파일\\1.XML 파일 분석\\P_" + file_id + "_TB.xml");
			docBuildFact = DocumentBuilderFactory.newInstance();
			docBuild = docBuildFact.newDocumentBuilder();
			Doc = docBuild.parse(file);
			Doc.getDocumentElement().normalize();

			Element root = Doc.getDocumentElement();
			NodeList rows = root.getElementsByTagName("ROWS");
			Element rowsElement = (Element) rows.item(0);
			NodeList row = rowsElement.getElementsByTagName("ROW");

			//P_(FILE_ID)_TB.xml의 row수만큼 반복.
			for (int i = 0; i < row.getLength(); i++) {
				Element rowElement = (Element) row.item(i);

				try {
					// P_PID
					NodeList pId = rowElement.getElementsByTagName("P_ID");
					Element pIdElement = (Element) pId.item(0);
					Node pIdNode = pIdElement.getFirstChild();

					if (p_id.equals(pIdNode.getNodeValue())) {
						// LICENSE_ID
						NodeList licenseIdList = rowElement.getElementsByTagName("LICENSE_ID");
						Element licenseIdElement = (Element) licenseIdList.item(0);
						Node licenseIdNode = licenseIdElement.getFirstChild();

						license_id = licenseIdNode.getNodeValue();

						return license_id;
					}
				} catch (NullPointerException e) {
					// TODO: handle exception
				}
			}

		} catch (NullPointerException e) {
			// TODO: handle exception
		} catch (Exception e) {
			// TODO: handle exception
		}

		return license_id;
	}

	//변경된 내용을 T_(FILE_ID)_TB.xml로 생성
	private static void makeXml(String file_id) {

		try {
			// XML 파일로 쓰기	
			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //정렬 스페이스4칸
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //들여쓰기
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes"); //doc.setXmlStandalone(true); 했을때 붙어서 출력되는부분 개행

			DOMSource source = new DOMSource(fDoc);
			StreamResult result = new StreamResult(new FileOutputStream(new File("C:\\03과제\\99.과제첨부파일\\1.XML 파일 분석\\T_" + file_id + "_TB.xml")));

			transformer.transform(source, result);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

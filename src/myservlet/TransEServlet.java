/**
 * TransEServlet.java
 */
package myservlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import myjavabean.model.MySessionContext;
import myjavabean.path.PublicVariable;
import myjavabean.transe.TransE;
/**
 * Servlet implementation class TransEServlet
 */
@WebServlet("/transeservlet")
public class TransEServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String sents;
	private String myId;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TransEServlet() {
        super();
        sents = null;
        myId = null;
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8"); // 设置接收信息的字符串编码方式
		response.setCharacterEncoding("utf-8"); // 设置返回信息的字符串编码方式
		PrintWriter out = response.getWriter(); // 获取返回信息的输出对象（句柄）
		try {
//			String positionsPara = request.getParameter("positions"); // 获取参数。所有正例句子的索引值。
			sents = request.getParameter("sents"); // 获取参数。用户输入的文本。
			HttpSession session = null; // 获取session
			try {
				myId = request.getParameter("myId");
				session = MySessionContext.getSession(myId);
			} catch (Exception e) {
				System.out.println("[TransEServlet]没有myId参数");
			}
			if (session == null) {
				session = request.getSession(false);
			} // 获取session
			if (sents == null || sents.equals("null")) {
				if (session != null) {
					session.setAttribute("resultFromTransE", "null");
					session.setAttribute("transEResult", null);
				}
				out.print(new String("ERROR|no data!"));
			}
			else {
				if (session == null) {
					out.print(new String("ERROR|no data!"));
				}
				else {
					System.out.println("[TransEServlet]SessionId: " + session.getId());
					TransE transETool = new TransE(); // 初始化TransE算法模型工具类对象
					String[] pos_sents = sents.split("。");
					ArrayList<String> transEResult = transETool.process(pos_sents); // 调用TransE算法模型对输入文本做细粒度分析。
					if(transEResult == null) {
						session.setAttribute("resultFromTransE", "null");
						session.setAttribute("transEResult", null);
						out.print(new String("ERROR|no data!")); // TransE处理结果为空字符串，返回没有信息。
					}
					else {
						String returnData = transEResult.get(0); // 整理返回信息文本。
						for(int i = 1; i < transEResult.size(); i++) {
							returnData += "|" + transEResult.get(i);
						}

						session.setAttribute("resultFromTransE", returnData);
						session.setAttribute("transEResult", transEResult);
						session.setAttribute("pos_sents", pos_sents);
//						PublicVariable.resultFromTransE = returnData;
						out.print(returnData); // 返回处理结果。
					}
				}
			}
//			String[] positions = positionsPara.trim().split(" "); // 获取参数
//			if(positions[0].equals("")) {
//				out.print(new String("ERROR|no data!")); // 获取的正例句子的索引值为空字符串，返回没有信息。
//			}
//			else {
//				TransE transETool = new TransE(); // 初始化TransE算法模型工具类对象
//				int[] positionsInt = new int[positions.length];
//				for(int i = 0; i < positions.length; i++) {
//					positionsInt[i] = Integer.parseInt(positions[i]); // 将接收的索引值从String类型（字符串）转化为int类型（整数）
//				}
//				ArrayList<String> transEResult = transETool.process(positionsInt, sents); // 调用TransE算法模型对输入文本做细粒度分析。
//				if(transEResult == null) {
//					out.print(new String("ERROR|no data!")); // TransE处理结果为空字符串，返回没有信息。
//				}
//				else {
//					String returnData = transEResult.get(0); // 整理返回信息文本。
//					for(int i = 1; i < transEResult.size(); i++) {
//						returnData += "|" + transEResult.get(i);
//					}
//
//					HttpSession session = request.getSession(false);
//					System.out.println("[TransEServlet]SessionId: " + session.getId());
//					
//					session.setAttribute("resultFromTransE", returnData);
////					PublicVariable.resultFromTransE = returnData;
//					out.print(returnData); // 返回处理结果。
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

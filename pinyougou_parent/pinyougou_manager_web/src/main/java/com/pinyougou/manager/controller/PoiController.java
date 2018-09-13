package com.pinyougou.manager.controller;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

import entity.Result;

//2

@RestController
@RequestMapping("/excel")
public class PoiController {

	private static final String EXCEL_XLS = "xls";

	private static final String EXCEL_XLSX = "xlsx";

	@Value("${file_server_url}")
	private String file_server_url;

	@Reference
	private SellerService sellerService;

	/**
	 * 
	 * 文件导出
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 */
	@RequestMapping("/outExcel")
	public void OutExcel(HttpServletResponse response) throws IOException {
		List<TbSeller> sellerList = sellerService.findAll();
		XSSFWorkbook book = new XSSFWorkbook();
		XSSFSheet sheet = book.createSheet();
		// 设置列宽
		sheet.setColumnWidth(0, 6 * 256);
		sheet.setColumnWidth(1, 14 * 256);
		sheet.setColumnWidth(2, 10 * 256);
		sheet.setColumnWidth(3, 16 * 256);
		sheet.setColumnWidth(4, 14 * 256);
		sheet.setColumnWidth(5, 15 * 256);
		sheet.setColumnWidth(6, 14 * 256);
		sheet.setColumnWidth(7, 17 * 256);
		sheet.setColumnWidth(8, 11 * 256);

		/**
		 * 设置样式(title)
		 */
		XSSFCellStyle styleTitle = book.createCellStyle();
		styleTitle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 水平居中
		styleTitle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		// 创建字体
		XSSFFont fontTitle = book.createFont();
		fontTitle.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗字体
		fontTitle.setColor(IndexedColors.DARK_BLUE.getIndex()); // 字体颜色
		fontTitle.setFontHeightInPoints((short) 18);
		// 加载字体
		styleTitle.setFont(fontTitle);

		/**
		 * 设置样式(head)
		 */
		XSSFCellStyle styleHead = book.createCellStyle();
		styleHead.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 水平居中
		styleHead.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		// 创建字体
		XSSFFont fontHead = book.createFont();
		fontHead.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗字体
		fontHead.setFontHeightInPoints((short) 14);
		// 加载字体
		styleHead.setFont(fontHead);

		/**
		 * 设置样式(body)
		 */
		XSSFCellStyle styleBody0 = book.createCellStyle();
		styleBody0.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 水平居中
		styleBody0.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		// 创建字体
		XSSFFont fontBody0 = book.createFont();
		fontBody0.setFontHeightInPoints((short) 11);
		// 加载字体
		styleBody0.setFont(fontBody0);

		XSSFCellStyle styleBody1 = book.createCellStyle();
		styleBody1.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 水平居中
		styleBody1.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		// 创建字体
		XSSFFont fontBody1 = book.createFont();
		fontBody1.setColor(IndexedColors.GREEN.getIndex()); // 字体颜色
		fontBody1.setFontHeightInPoints((short) 11);
		// 加载字体
		styleBody1.setFont(fontBody1);

		XSSFCellStyle styleBody2 = book.createCellStyle();
		styleBody2.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 水平居中
		styleBody2.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		// 创建字体
		XSSFFont fontBody2 = book.createFont();
		fontBody2.setColor(IndexedColors.RED.getIndex()); // 字体颜色
		fontBody2.setFontHeightInPoints((short) 11);
		// 加载字体
		styleBody2.setFont(fontBody2);

		/**
		 * 标题
		 */
		// 创建合并单元格
		CellRangeAddress region = new CellRangeAddress(0, 0, 0, 8);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
		// 插入合并单元格
		sheet.addMergedRegion(region);
		XSSFRow titleRoW = sheet.createRow(0);
		XSSFCell titleCell = titleRoW.createCell(0);
		titleCell.setCellValue("品优购商家信息表");
		titleCell.setCellStyle(styleTitle);

		/**
		 * 表头
		 */
		// 添加表头对应的信息，把对应的表头信息放到数组head中
		String[] head = { "序号", "商家ID", "密码", "公司名称", "店铺名称", "联系人姓名", "公司电话", "入驻日期", "状态" };
		// 把表头对应的信息放到表格对应位置
		XSSFRow row = sheet.createRow(1);
		for (int i = 0; i < head.length; i++) {
			// 把数组中的元素依次放进去
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(head[i]);
			cell.setCellStyle(styleHead);
		}

		/**
		 * 正文
		 */
		for (int i = 1; i <= sellerList.size(); i++) {
			TbSeller seller = sellerList.get(i - 1);
			String[] body = { String.valueOf(i), seller.getSellerId(), "******", seller.getName(), seller.getNickName(),
					seller.getLinkmanName(), seller.getTelephone(),
					new SimpleDateFormat("yyyy年MM月dd日").format(seller.getCreateTime()), seller.getStatusStr() };
			XSSFRow newRow = sheet.createRow(i + 1);
			for (int j = 0; j < body.length; j++) {
				XSSFCell newCell = newRow.createCell(j);
				newCell.setCellValue(body[j]);
				if (j == 8 && seller.getStatus().equals("1")) {
					newCell.setCellStyle(styleBody1);
					continue;
				} else if (j == 8 && seller.getStatus().equals("2")) {
					newCell.setCellStyle(styleBody2);
					continue;
				} else {
					newCell.setCellStyle(styleBody0);
				}
			}
		}
		// book.write(new FileOutputStream("d:\\a.xlsx"));
		OutputStream outputStream = response.getOutputStream();
		book.write(outputStream);
		outputStream.flush();
		outputStream.close();
	}

	/**
	 * 表格上传到服务器
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/inExcel")
	public Result uploadFile(MultipartFile file) throws IOException {
		try {
			// 判断是否为Excel文件
			checkExcel(file);
			// 得到对应版本的Excel文件
			Workbook book = getWorkbook(file.getInputStream(), file);
			InExcel(book);
			return new Result(true, "导入成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
		}
	}

	/**
	 * 
	 * 文件导入
	 * 
	 */
	public void InExcel(Workbook book) {
		try {
			Sheet sheet = book.getSheetAt(0);
			TbSeller seller = new TbSeller();
			for (int i = 2; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				for (int j = 1; j < 9; j++) {
					Cell cell = row.getCell(j);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					String value = cell.getStringCellValue();
					switch (j) {
					case 1:
						seller.setSellerId(value);
						break;
					case 2:
						seller.setPassword(value);
						break;
					case 3:
						seller.setName(value);
						break;
					case 4:
						seller.setNickName(value);
						break;
					case 5:
						seller.setLinkmanName(value);
						break;
					case 6:
						seller.setTelephone(value);
						break;
					case 7:
						seller.setCreateTime(new SimpleDateFormat("yyyy年MM月dd日").parse(value));
						break;
					case 8:
						if ("未审核".equals(value)) {
							seller.setStatus("0");
						} else if ("审核通过".equals(value)) {
							seller.setStatus("1");
						} else {
							seller.setStatus("2");
						}
						break;
					}
				}
				boolean flag = hasBean(seller.getSellerId());
				if (flag) {
					sellerService.update(seller);
				} else {
					sellerService.add(seller);
				}
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 判断是否为Excel
	public void checkExcel(MultipartFile file) {
		if (!(file.getOriginalFilename().endsWith(EXCEL_XLS) || file.getOriginalFilename().endsWith(EXCEL_XLSX))) {
			throw new RuntimeException("文件不是Excel");
		}
	}

	// 判断Excel版本
	public Workbook getWorkbook(InputStream inputStream, MultipartFile file) {
		Workbook book = null;
		try {
			if (file.getOriginalFilename().endsWith(EXCEL_XLS)) { // Excel 2003
				book = new HSSFWorkbook(inputStream);
			} else if (file.getOriginalFilename().endsWith(EXCEL_XLSX)) { // Excel 2007/2010
				book = new XSSFWorkbook(inputStream);
			}
			return book;
		} catch (Exception e) {
			throw new RuntimeException("版本不支持");
		}
	}

	// 判断表格当前行是否存在
	public boolean hasBean(String sellerId) {
		TbSeller seller = sellerService.findOne(sellerId);
		if (seller == null) {
			return false;
		} else {
			return true;
		}

	}
}

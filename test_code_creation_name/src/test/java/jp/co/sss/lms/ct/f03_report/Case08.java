package jp.co.sss.lms.ct.f03_report;

import static jp.co.sss.lms.ct.util.WebDriverUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import jp.co.sss.lms.ct.util.TestAccount;

/**
 * 結合テスト レポート機能
 * ケース08
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース08 受講生 レポート修正(週報) 正常系")
public class Case08 {

	/** 前処理 */
	@BeforeAll
	static void before() {
		createDriver();
	}

	/** 後処理 */
	@AfterAll
	static void after() {
		closeDriver();
	}

	@Test
	@Order(1)
	@DisplayName("テスト01 トップページURLでアクセス")
	void test01() {
		//LMS　ログインページのトップ
		goTo("http://localhost:8080/lms/");
		//現在URLの取得
		String currentUrl = webDriver.getCurrentUrl();
		//タイトルを取得(WebElement)
		WebElement loginPageTitleElm = webDriver.findElement(By.tagName("h2"));
		//現在アドレスと検証するアドレスを比較
		assertEquals("http://localhost:8080/lms/",currentUrl,"検証するURLと違います。");
		//ログインページトップのタイトルと一致しているか確認
		assertEquals("ログイン", loginPageTitleElm.getText(),"ページタイトルが違います。");
		//エビデンス取得
		getEvidence(new Object() {},"ログイン画面");
	}

	@Test
	@Order(2)
	@DisplayName("テスト02 初回ログイン済みの受講生ユーザーでログイン")
	void test02() {
		//テスト用のアカウントクラス
		TestAccount user = new TestAccount();
		//フォームの属性を取得
		WebElement loginFormId = webDriver.findElement(By.id("loginId"));
		WebElement loginFormPw = webDriver.findElement(By.id("password"));
		//フォームのクリア
		loginFormId.clear();
		loginFormPw.clear();
		//フォームに入力
		loginFormId.sendKeys(user.getUserId());
		loginFormPw.sendKeys(user.getPassword());
		//スクリーンショットによるエビデンスの取得（入力内容)
		getEvidence(new Object() {},"入力内容");
		//ログインボタン押下
		webDriver.findElement(By.className("btn")).click();
		//ログイン成功、画面遷移後のスクリーンショットによるエビデンスの取得
		getEvidence(new Object() {} , "ログイン成功");
		//画面遷移後のURLの取得
		String loginSuccessUrl = webDriver.getCurrentUrl();
		//画面遷移後のページタイトルの取得
		WebElement pageTitle = webDriver.findElement(By.className("breadcrumb"));

		//各要素の期待値との比較
		//URLの比較
		assertEquals("http://localhost:8080/lms/course/detail",loginSuccessUrl,"テスト対象URLと一致していません。");
		//遷移後のページの見出しの比較
		assertEquals("コース詳細",pageTitle.getText(),"期待値のページではありません。");
	}

	@Test
	@Order(3)
	@DisplayName("テスト03 提出済の研修日の「詳細」ボタンを押下しセクション詳細画面に遷移")
	void test03()  {
		getEvidence(new Object() {} , "ステータス状態");
		//セクション詳細の行をすべて取得するロケータ
		By tableRowSelector = By.cssSelector("table.sctionList tbody tr");
		//詳細ボタンのロケータ
		By detailButtonSelector = By.cssSelector("input[type='submit'][value*='詳細']");
		//週報ボタンのロケータ
		By weeklyReportSelector = By.cssSelector("input[type='submit'][value*='週報']");
		//セクション詳細の要素数を取得
		int elementListSize = 
				webDriver.findElements(tableRowSelector).size();
		//検出した行のindexを格納

		for(int i=0 ; i< elementListSize ; i++) {
			//コース詳細からをセクション詳細入手しリストに追加
			List<WebElement> courseSectionList = 
					webDriver.findElements(tableRowSelector);
			//各行をインデックスで指定
			WebElement sectionRow = courseSectionList.get(i);
			//セクション詳細リストから"提出済み"を検出
			if(!sectionRow.getText().contains("提出済み")){
				continue;
			}

			//ステータスが提出済みの行の詳細ボタンを取得
			WebElement submitButton =sectionRow.findElement(detailButtonSelector);
			//詳細ボタン押下
			submitButton.click();
			//ページ遷移完了まで３秒待機　対象はsectionテーブル
			visibilityTimeout(By.id("section"), 3);

			getEvidence(new Object() {} , "セクション詳細画面");

			//週報ボタンを探す
			List<WebElement> weeklyReportButton = webDriver.findElements(weeklyReportSelector);
			//findElementsはリストを返すのでisEmptyで週報ボタンが存在するか確かめることができる
			if(weeklyReportButton.isEmpty()) {
				//trueの場合ページバックして次のループへ
				webDriver.navigate().back();
				continue;
			}
			try {
				//存在はしているが、表示されてないだけの場合はisDisplayedで確認、表示されていたらindexを返す
				if(webDriver.findElement(weeklyReportSelector).isDisplayed()) {
					break;
				}
				//findElementは例外をスローするのでキャッチ
			}catch(NoSuchElementException e) {
				webDriver.navigate().back();
			}
		}
		//現在URLの取得
		String URL = webDriver.getCurrentUrl();
		//期待値との比較(URL)
		assertEquals("http://localhost:8080/lms/section/detail",URL,"期待値のページと異なります。");
		//週報ボタンが表示されてるかでテスト
		assertTrue(webDriver.findElement(weeklyReportSelector).isDisplayed(),"要素が表示されていません。");
	}

	@Test
	@Order(4)
	@DisplayName("テスト04 「確認する」ボタンを押下しレポート登録画面に遷移")
	void test04() {
		//週報ボタンのロケータ
		By weeklyReportSelector = By.cssSelector("input[type='submit'][value*='週報']");
		//週報ボタンの要素を取得
		WebElement weeklyReportButton = webDriver.findElement(weeklyReportSelector);
		//週報ボタンを押下
		weeklyReportButton.click();
		//現在URL取得
		String url = webDriver.getCurrentUrl();
		//期待値との比較
		assertEquals("http://localhost:8080/lms/report/regist",url,"期待値のページと違います");
		//エビデンス取得
		getEvidence(new Object() {} , "レポート登録画面");
	}

	@Test
	@Order(5)
	@DisplayName("テスト05 報告内容を修正して「提出する」ボタンを押下しセクション詳細画面に遷移")
	void test05() throws InterruptedException {
		getEvidence(new Object() {} , "週報レポート変更前");
		//フォームの入力部分の要素をまとめて取得
		List<WebElement> weeklyReportForm = webDriver.findElements(By.className("form-control"));
		//フォームのクリア
		//form.getTagName()でタグ名を取得してスイッチ
		for(WebElement form : weeklyReportForm) {
			switch(form.getTagName()) {
			case "input":	form.clear();
							break;
			case "select":	Select selectForm = new Select(form);
							selectForm.selectByIndex(0);
							break;
			case "textarea":form.clear();
			}
			visibilityTimeout(By.className("form-control"), 3);
		}
		//エビデンス取得
		getEvidence(new Object() {} , "週報フォームクリア後");
		//各フォームに変更を加える
		//form.getTagName()でタグ名を取得してスイッチ
		for(WebElement form : weeklyReportForm) {
			switch(form.getTagName()) {
			case "input":	form.sendKeys("変更テスト後");
							break;
			case "select":	Select selectForm = new Select(form);
							selectForm.selectByIndex(3);
							break;
			case "textarea":
				if(form.getAttribute("id").equals("content_2")) {
					form.sendKeys("10");
				}else {
					form.sendKeys("変更テスト後");
				}
			}
			visibilityTimeout(By.className("form-control"), 3);
		}
		for(WebElement form : weeklyReportForm) {
			switch(form.getTagName()) {
			case "input":	assertEquals("変更テスト後",form.getAttribute("value"),"input:テキストが期待値と違います");
							break;
			case "select":	Select selectForm = new Select(form);
							selectForm.selectByIndex(3);
							break;
			case "textarea":
				if(form.getAttribute("id").equals("content_2")) {
					assertEquals("10",form.getAttribute("value"),"number:テキストが期待値と違います");
				}else {
					assertEquals("変更テスト後",form.getAttribute("value"),"textarea:テキストが期待値と違います");
				}
			}
			visibilityTimeout(By.className("form-control"), 3);
		}
		//エビデンス取得
		getEvidence(new Object() {} , "週報レポート入力後");
		//300px↓にスクロール
		scrollBy("300");
		//提出するボタンの要素取得
		var submitButton = webDriver.findElement(By.cssSelector(".btn.btn-primary"));
		//提出するボタンをクリック
		submitButton.click();
		
		visibilityTimeout(By.id("wrap"), 3);
	
	}
	@Test
	@Order(6)
	@DisplayName("テスト06 上部メニューの「ようこそ○○さん」リンクからユーザー詳細画面に遷移")
	void test06(){
		var nameButton = webDriver.findElement(By.cssSelector("a[href='/lms/user/detail']"));
		
		nameButton.click();
		
		String Url = webDriver.getCurrentUrl();
		
		assertEquals("http://localhost:8080/lms/user/detail",Url,"期待値のページと異なります");
		
		getEvidence(new Object() {} , "ユーザー詳細画面");
	}

	@Test
	@Order(7)
	@DisplayName("テスト07 該当レポートの「詳細」ボタンを押下しレポート詳細画面で修正内容が反映される")
	void test07(){
		//週報の日報を検索するxpath
		By weeklyReportSelect = By.xpath("//tr[contains(., '週報')]");
		//「変更テスト後」が含まれている行を検索するxpath
		By weeklyReportSearchWord = By.xpath(".//p[contains(text(), '変更テスト後')]");
		//行の詳細ボタンを検索するxpath
		By weeklyReportDetailButton = 
				By.xpath("//tr[contains(., '週報')]//form[@action='/lms/report/detail']/input");
		
		int count = webDriver.findElements(weeklyReportSelect).size();
		
		
		
		for(int i=0 ; i<count ;i++) {
		var weeklyReportRows = webDriver.findElements(weeklyReportSelect);
		
		var weeklyReportDetail =
				weeklyReportRows.get(i).findElement(weeklyReportDetailButton);
		
		scrollBy("1000");
		
		weeklyReportDetail.click();
		
		var weektest = webDriver.findElements(weeklyReportSearchWord);
		
		System.out.println(weektest.size());
		
		webDriver.navigate().back();
		}
	}
}

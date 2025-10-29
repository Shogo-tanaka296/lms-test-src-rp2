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
import org.openqa.selenium.WebElement;

import jp.co.sss.lms.ct.util.TestAccount;

/**
 * 結合テスト レポート機能
 * ケース07
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース07 受講生 レポート新規登録(日報) 正常系")
public class Case07 {

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
	@DisplayName("テスト03 未提出の研修日の「詳細」ボタンを押下しセクション詳細画面に遷移")
	void test03() {
		getEvidence(new Object() {},"未提出確認");
		//コース詳細からをセクション詳細入手しリストに追加
        List<WebElement> courseSectionList = 
                webDriver.findElements(By.cssSelector("table.sctionList tbody tr"));
        //セクション詳細リストから"未提出"を検出(テストなので１件)
        //未提出を検出した行から「詳細」ボタンの要素を入手しクリック
        for(WebElement value:courseSectionList) {
        	if(value.getText().contains("未提出")) {
        		value.findElement(By.cssSelector
        				("input[type='submit'].btn.btn-default")).click();
        		break ;//未提出が見つかったらその行の詳細ボタンをクリックしてループを抜ける
        	}
        }
		//テスト用URL取得
		String currentURL = webDriver.getCurrentUrl();
		assertEquals("http://localhost:8080/lms/section/detail",currentURL,"期待値のページではありません。");
		//エビデンス取得
        getEvidence(new Object() {},"セクション詳細画面");
	}

	@Test
	@Order(4)
	@DisplayName("テスト04 「提出する」ボタンを押下しレポート登録画面に遷移")
	void test04() {
		//日報【デモ】登録ページへ遷移
		webDriver.findElement(By.cssSelector("input[type='submit'][value='日報【デモ】を提出する']")).click();
		//テスト用URL取得
		String currentURL = webDriver.getCurrentUrl();
		//要素のテスト
		assertEquals("http://localhost:8080/lms/report/regist",currentURL,"期待値のページではありません。");
		//エビデンスの取得
		getEvidence(new Object() {},"日報【デモ】登録ページ");
	}

	@Test
	@Order(5)
	@DisplayName("テスト05 報告内容を入力して「提出する」ボタンを押下し確認ボタン名が更新される")
	void test05() {
		//エビデンス取得
		getEvidence(new Object() {},"フォーム入力前");
		//テスト用テキストエリア属性取得
		WebElement submitButton = 
				webDriver.findElement(By.cssSelector("button.btn.btn-primary"));
		visibilityTimeout(By.id("content_0"), 10);
		WebElement reportTextArea = webDriver.findElement(By.id("content_0"));
		//テキストエリアのクリア
		reportTextArea.clear();
		//テキストエリアに文字列挿入
		reportTextArea.sendKeys("テスト用デモ入力");
		//エビデンス取得
		getEvidence(new Object() {},"フォーム入力後");
		//「提出する」ボタン押下
		submitButton.click();
		//ボタンの要素を取得
		WebElement reportButton = 
				webDriver.findElement(By.cssSelector("input[type='submit'][value='提出済み日報【デモ】を確認する']"));
		assertEquals("提出済み日報【デモ】を確認する",reportButton.getAttribute("value"),"ボタンの表示が期待値と異なります");
		
		//エビデンス取得
		getEvidence(new Object() {},"日報【登録】後");
		
	}

}

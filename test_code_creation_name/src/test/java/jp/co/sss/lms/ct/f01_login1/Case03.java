package jp.co.sss.lms.ct.f01_login1;

import static jp.co.sss.lms.ct.util.WebDriverUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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
 * 結合テスト ログイン機能①
 * ケース03
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース03 受講生 ログイン 正常系")
public class Case03 {

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
				//ログインフォームのラベルの属性を取得。classで検索しているので複数ヒットするためリストに格納
				List<WebElement> loginPageFormElm = new ArrayList<>();
				loginPageFormElm.addAll(webDriver.findElements(By.className("col-lg-2")));
				//テスト対象と比較するワードを配列に登録
				String[] assertWords = {"ログインID","パスワード"};
				//ログインId入力欄
				WebElement loginId = webDriver.findElement(By.id("loginId"));
				//ログインパスワード入力欄
				WebElement loginPw = webDriver.findElement(By.id("password"));
				//ログインボタン
				WebElement loginBtn = webDriver.findElement(By.className("btn"));
				//パスワードリセットリンク
				WebElement resetLink = webDriver.findElement(By.linkText("パスワードを忘れた方はこちら"));
				//各種パネル
				List<WebElement> loginPagePanelElm = new ArrayList<>();
				loginPagePanelElm.addAll(webDriver.findElements(By.className("panel-title")));
				
				//現在アドレスと検証するアドレスを比較
				assertEquals("http://localhost:8080/lms/",currentUrl,"検証するURLと違います。");
				
				//ログインページトップのタイトルと一致しているか確認
				assertEquals("ログイン", loginPageTitleElm.getText(),"ページタイトルが違います。");
				//ログインページのログインフォームのラベルと一致しているか確認
				for(int i=0 ;i< assertWords.length; i++) {
					assertEquals(assertWords[i],loginPageFormElm.get(i).getText());
				}
				//各要素のテスト
				assertTrue(loginId.isDisplayed(),"ID入力フォームが表示されていません。");
				assertTrue(loginPw.isDisplayed(),"PassWord入力フォームが表示されていません。");
				assertTrue(loginBtn.isDisplayed(),"ログインボタンが表示されていません。");
				assertTrue(resetLink.isDisplayed(),"パスワードリセットページへのリンクが表示されていません。");
				
				//エビデンス取得
				getEvidence(new Object() {});
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

}

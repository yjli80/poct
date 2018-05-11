# poct api
## Token 的获取和使用

项目自建了一个 oauth2 的服务，并使用 [JWT](https://jwt.io/) 作为token。

1. 使用 clientId 和 clientSecret 取得 token：

`curl --user clientId:clientSecret localhost:8989/oauth/token -d grant_type=password -d username=test -d password=test`

`{
  "access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiUE9DVC1BUEktTkZZWS0yMDE4Il0sInVzZXJfbmFtZSI6InRlc3QiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNTI2MDI0NzA1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMjI0NDRhYTYtZjc2ZS00Y2RmLWIwZGItZWQyMTg3ZTEyODQ1IiwiY2xpZW50X2lkIjoiUE9DVC1ORllZLTIwMTgifQ.3cVDIH-EbyF2YgD3bkhHyx-_kyvi3MfBDmlxAlVPzzI",
  "token_type":"bearer",
  "expires_in":3599,
  "scope":"read write",
  "jti":"22444aa6-f76e-4cdf-b0db-ed2187e12845"
}`

* access_token: Base64 编码的 token，由 "." 分隔为 HEADER，PAYLOAD，VERIFY SIGNATURE三部分，格式参考 [jwt.io](https://jwt.io/)。
  第一、第二部分解码之后如下所示。
  第二部分中的 user_name 表示用户名，exp 表示何时超时（从 1970-01-01 00:00:00 开始的秒数）
  
  {"alg":"HS256","typ":"JWT"}
  
  {"aud":["POCT-API-NFYY-2018"],"user_name":"test","scope":["read","write"],"exp":1526024705,"authorities":["ROLE_USER"],"jti":"22444aa6-f76e-4cdf-b0db-ed2187e12845","client_id":"POCT-NFYY-2018"}
  
* token_type: 类型
* expires_in: 距离超时的秒数

2. 使用 token 调用 api

`curl localhost:8989/api/users -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiUE9DVC1BUEktTkZZWS0yMDE4Il0sInVzZXJfbmFtZSI6InRlc3QiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNTI2MDI0NzA1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMjI0NDRhYTYtZjc2ZS00Y2RmLWIwZGItZWQyMTg3ZTEyODQ1IiwiY2xpZW50X2lkIjoiUE9DVC1ORllZLTIwMTgifQ.3cVDIH-EbyF2YgD3bkhHyx-_kyvi3MfBDmlxAlVPzzI"`

成功时返回

	{
	  "_embedded" : {
	    "users" : [ {
	      "username" : "test",
	      "name" : "test",
	      "gender" : null,
	      "birthDate" : null,
	      "mobile" : null,
	      "email" : null,
	      "_links" : {
	        "self" : {
	          "href" : "http://localhost:8989/api/users/1"
	        },
	        "user" : {
	          "href" : "http://localhost:8989/api/users/1"
	        },
	        "roles" : {
	          "href" : "http://localhost:8989/api/users/1/roles"
	        }
	      }
	    }]
	  },
	  "_links" : {
	    "self" : {
	      "href" : "http://localhost:8989/api/users{?page,size,sort}",
	      "templated" : true
	    },
	    "profile" : {
	      "href" : "http://localhost:8989/api/profile/users"
	    },
	    "search" : {
	      "href" : "http://localhost:8989/api/users/search"
	    }
	  },
	  "page" : {
	    "size" : 20,
	    "totalElements" : 1,
	    "totalPages" : 1,
	    "number" : 0
	  }
	}
  
  失败时（例如超时）返回 401，错误信息格式如下例所示：
  
  `{ error: "invalid_token", error_description: "Access token expired: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiUE9DVC1BUEktTkZZWS0yMDE4Il0sInVzZXJfbmFtZSI6InRlc3QiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNTI2MDI0NzA1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMjI0NDRhYTYtZjc2ZS00Y2RmLWIwZGItZWQyMTg3ZTEyODQ1IiwiY2xpZW50X2lkIjoiUE9DVC1ORllZLTIwMTgifQ.3cVDIH-EbyF2YgD3bkhHyx-_kyvi3MfBDmlxAlVPzzI" }`
  
  + Java 取得认证 token 的 [例子源代码](https://github.com/yjli80/poct/blob/master/poct-api/src/test/java/nfyy/poct/test/JWTAuthenticationTest.java)
  + jQuery 取得认证 token 和调用 api 的 [例子](https://github.com/yjli80/poct/blob/master/poct-api/src/main/resources/static/index.html)
  
  ## 接口

// A JWT plugin for demo
// 将获取到的 token 保存到 localStorage.token
(function($) {

	$.jwt = function(element, options) {

		var defaults = {
			url : 'http://211.66.87.97:8989/oauth/token',
			username : 'admin',
			password : 'adscret',
			clientId : 'POCT-NFYY-2018',
			clientScret : 'QCxbvBlaZgVjsOfnTx',
			grantType : 'password'
		},

		plugin = this,
		$element = $(element), element = element;
		
		plugin.token = null;
		plugin.tokenArray = null;
		plugin.headerDecoded = null;
		plugin.payloadDecoded = null;
		plugin.header = {};
		plugin.payload = {};
		
		plugin.settings = {};
		
		plugin.init = function() {
			plugin.settings = $.extend({}, defaults, options);
			
			if (!plugin.isValid()) {
				plugin.auth();
			}
		}

		plugin.show = function() {
			if ($element)
			$element.append("<b>Token:</b><span>" + plugin.token + "</span><br/>")
				.append("<b>header:</b><span>" + plugin.headerDecoded + "</span><br/>")
				.append("<b>payload:</b><span>" + plugin.payloadDecoded + "</span><br/>")
				.append("<b>expired:</b><span>" + plugin.payload.exp + "</span><br/>")
				.append("<b>expired in:</b><span>" + plugin.secondsBeforeExpire() + "</span>");
		}
		
		plugin.auth = function() {
			$.ajax({
				url : plugin.settings.url,
				method : 'POST',
				data : {
					username : plugin.settings.username,
					password : plugin.settings.password,
					grant_type : plugin.settings.grantType
				},
				beforeSend : function(xhr) {
					xhr.setRequestHeader('Authorization', "Basic "
						+ btoa(plugin.settings.clientId + ":" + plugin.settings.clientScret));
				},
				success : function(data, status, xhr) {
					localStorage.token = data.access_token;
					parse(localStorage.token);
					plugin.show();
				},
				error : function(xhr, status, e) {
					console.log(xhr);
				}
			});
		}

		function parse(access_token) {
			plugin.token = access_token;
			plugin.tokenArray = plugin.token.split(".");
			plugin.headerDecoded = atob(plugin.tokenArray[0]);
			plugin.payloadDecoded = atob(plugin.tokenArray[1]);
			plugin.header = JSON.parse(plugin.headerDecoded);
			plugin.payload = JSON.parse(plugin.payloadDecoded);
		}

		plugin.secondsBeforeExpire = function() {
			var now = Math.round(new Date().getTime() / 1000);
			return plugin.payload.exp - now;
		}

		plugin.isExpired = function() {
			return plugin.secondsBeforeExpire() < 0;
		}

		plugin.isValid = function() {
			return plugin.token && isExpired();
		}

		plugin.init();

	}

	$.fn.jwt = function(options) {

		return this.each(function() {
			if (undefined == $(this).data('jwt')) {
				var plugin = new $.jwt(this, options);
				$(this).data('jwt', plugin);
			}
		});

	}

})(jQuery);
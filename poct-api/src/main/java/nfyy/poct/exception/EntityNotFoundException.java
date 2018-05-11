package nfyy.poct.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public EntityNotFoundException(Class<?> clz, String property, Object value) {
		super(String.format("没有找到资源：%s[%s=%s]", clz.getName(), property, value));
	}

}

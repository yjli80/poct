package nfyy.poct.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface RoleRepository extends PagingAndSortingRepository<Role, String> {

}

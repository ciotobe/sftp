package jp.co.jri.epix.sftp.repo;

import jp.co.jri.epix.sftp.entity.ApiAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiAccessRepository extends JpaRepository<ApiAccess, Long> {
    Optional<ApiAccess> findApiAccessByApplication(String application);

    List<ApiAccess> findAllByApplication(String application);
}

package jp.co.jri.epix.sftp.repo;

import jp.co.jri.epix.sftp.entity.ApiAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApiAccessRepository extends JpaRepository<ApiAccess, Long> {
    Optional<ApiAccess> findApiAccessByBranch(String branch);

    List<ApiAccess> findAllByBranch(String branch);
}

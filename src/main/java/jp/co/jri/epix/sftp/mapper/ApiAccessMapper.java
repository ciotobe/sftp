package jp.co.jri.epix.sftp.mapper;

import jp.co.jri.epix.sftp.model.ApiAccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApiAccessMapper{
    //ApiAccess findApiAccessByApplication(@Param("application") String application);

    List<ApiAccess> findAllByApplication(@Param("application") String application);
}

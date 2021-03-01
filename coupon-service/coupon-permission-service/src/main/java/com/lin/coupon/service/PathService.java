package com.lin.coupon.service;

import com.lin.coupon.dao.PathRepository;
import com.lin.coupon.entity.Path;
import com.lin.coupon.vo.CreatePathRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>路径相关的服务功能实现</h1>
 */
@Slf4j
@Service
public class PathService {

    @Autowired
    private PathRepository pathRepository;


    /**
     * <h2>添加新的path到数据表中</h2>
     *
     * @param request {@link CreatePathRequest}
     * @return Path表的主键
     */
    public List<Integer> createPath(CreatePathRequest request) {
        List<CreatePathRequest.PathInfo> pathInfos = request.getPathInfos();
        List<CreatePathRequest.PathInfo> validRequests =
                new ArrayList<>(request.getPathInfos().size());

        //从path表根据服务名称获取Path数据
        List<Path> currentPaths = pathRepository.findAllByServiceName(
                pathInfos.get(0).getServiceName()
        );

        if (!CollectionUtils.isEmpty(currentPaths)) {

            //判断请求体的数据是否存在path表中
            for (CreatePathRequest.PathInfo pathInfo : pathInfos) {
                boolean isValid = true;
                for (Path currentPath : currentPaths) {
                    if (currentPath.getPathPattern()
                            .equals(pathInfo.getPathPattern()) &&
                            currentPath.getHttpMethod().equals(pathInfo.getHttpMethod())) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    validRequests.add(pathInfo);
                }
            }
        } else {
            validRequests = pathInfos;
        }

        //构建path对象保存到path表中
        List<Path> paths = new ArrayList<>(validRequests.size());
        validRequests.forEach(p -> paths.add(new Path(
                p.getPathPattern(),
                p.getHttpMethod(),
                p.getPathName(),
                p.getServiceName(),
                p.getOpMode()
        )));
        return pathRepository.saveAll(paths)
                .stream().map(Path::getId).collect(Collectors.toList());
    }

}


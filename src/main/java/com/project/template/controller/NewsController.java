package com.project.template.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.template.common.PageVO;
import com.project.template.common.Result;
import com.project.template.entity.News;
import com.project.template.service.NewsService;
import com.project.template.utils.Utils;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Resource
    private NewsService newsService;

    @ApiOperation(value = "列表",notes = "列表")
    @GetMapping("/list")
    private Result<List<News>> list(){
        return new Result().success(newsService.list());
    }

    @GetMapping("/page")
    public Result<PageVO<News>> findPage(
            @RequestParam Map<String,Object> query,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        Page<News> page= newsService.page(query,pageNum,pageSize);
        return new Result().success(new PageVO<>(page));
    }

    @PostMapping("/add")
//    @Validated
    public Result add(@RequestBody News news){
        news.setCreateTime(new Date());
        news.setViews(0);
        news.setUser(Utils.getUser().getUsername());
        newsService.save(news);
        return new Result<>().success();
    }

    @PutMapping("/update")
//    @Validated
    public Result updateById(@RequestBody News news){
        newsService.updateById(news);
        return new Result<>().success();
    }

    @DeleteMapping("/delBatch")
    public Result delBatch(@RequestBody List<Integer> ids){
        newsService.removeByIds(ids);
        return new Result().success();
    }

    /**
     * 浏览量+1
     * @param id
     * @return
     */
    @GetMapping("getById")
    public Result<News> getById(@RequestParam("id") Integer id){
        News news = newsService.getById(id);
        //如果为null，要给初始值
        if(news.getViews()==null){
            news.setViews(1);
        }else{
            news.setViews(news.getViews()+1);
        }
        newsService.updateById(news);
        return new Result().success(news);
    }

}
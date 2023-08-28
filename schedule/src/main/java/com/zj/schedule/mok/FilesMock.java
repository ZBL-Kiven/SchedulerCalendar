package com.zj.schedule.mok;

import androidx.annotation.Nullable;

import com.zj.api.eh.EHParam;
import com.zj.api.mock.MockAble;
import com.zj.schedule.data.entity.ScheduleFileInfo;

import java.util.ArrayList;
import java.util.List;

public class FilesMock implements MockAble<List<ScheduleFileInfo>> {

    private final Long[] sizeTest = {-1L, 0L, 532L, 12314L, 3823782L, 7872648L, 873462236L, 6773237293874L, 8743823824829L, 3042319L};

    @Nullable
    @Override
    public List<ScheduleFileInfo> getMockData(EHParam ehParam) {
        List<ScheduleFileInfo> lst = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ScheduleFileInfo fileInfo = new ScheduleFileInfo();
            fileInfo.setUpdateTime(System.currentTimeMillis());
            fileInfo.setUploaderName("Test");
            fileInfo.setUrl("https://img2.baidu.com/it/u=383558092,2478787266&fm=253&fmt=auto&app=138&f=JPEG?w=236&h=133");
            fileInfo.setCreateTime(System.currentTimeMillis());
            fileInfo.setName("test-files" + i + ".jpeg");
            fileInfo.setSize(sizeTest[i]);
            lst.add(fileInfo);
        }
        return lst;
    }
}

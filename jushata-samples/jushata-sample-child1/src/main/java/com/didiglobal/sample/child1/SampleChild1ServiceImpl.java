package com.didiglobal.sample.child1;

import com.didiglobal.sample.api.SampleBean;
import com.didiglobal.sample.api.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("sampleChildService")
public class SampleChild1ServiceImpl implements SampleService {

    @Autowired
    @Qualifier("javaSampleBean")
    private SampleBean             javaSampleBean;

    @Autowired
    @Qualifier("xmlSampleBean")
    private SampleBean             xmlSampleBean;

    @Autowired
    private SampleChild1Properties sampleChild1Properties;

    @Override
    public String echo() {

        return "helloworld, <br/>" //
               + "" + javaSampleBean + "<br/>" //
               + "" + xmlSampleBean + "<br/>" //
               + "" + sampleChild1Properties + "<br/>" //
               + "" + sampleChild1Properties.getClass().getClassLoader() + "<br/>" //
               + "" + this;
    }
}

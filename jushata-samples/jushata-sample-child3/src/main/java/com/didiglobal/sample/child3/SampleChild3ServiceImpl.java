package com.didiglobal.sample.child3;

import com.didiglobal.sample.api.SampleBean;
import com.didiglobal.sample.api.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("sampleChildService")
public class SampleChild3ServiceImpl implements SampleService {

    @Autowired
    @Qualifier("javaSampleBean")
    private SampleBean javaSampleBean;

    @Autowired
    @Qualifier("xmlSampleBean")
    private SampleBean             xmlSampleBean;

    @Autowired
    private SampleChild3Properties sampleChild3Properties;

    @Override
    public String echo() {

        return "helloworld, <br/>" //
               + "" + javaSampleBean + "<br/>" //
               + "" + xmlSampleBean + "<br/>" //
               + "" + sampleChild3Properties + "<br/>" //
               + "" + sampleChild3Properties.getClass().getClassLoader() + "<br/>" //
               + "" + this;
    }
}

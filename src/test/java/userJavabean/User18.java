/*
 * Copyright 360buy
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package userJavabean;


import java.util.List;
import java.util.Map;
import java.util.Set;

public class User18 {

    private List<Map<String, String>> list;
    private Set<Map<String, String>> set;
    private Map<String, Map<String,String>> map;

    public List<Map<String, String>> getList() {
        return list;
    }

    public void setList(List<Map<String, String>> list) {
        this.list = list;
    }

    public Set<Map<String, String>> getSet() {
        return set;
    }

    public void setSet(Set<Map<String, String>> set) {
        this.set = set;
    }

    public Map<String, Map<String, String>> getMap() {
        return map;
    }

    public void setMap(Map<String, Map<String, String>> map) {
        this.map = map;
    }
}

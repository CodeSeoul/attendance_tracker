/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Modified by Learn Teach Code Seoul, Bryan "Beege" Berry
 */
package org.learnteachcode.seoul.attendance.tracker.util;

import org.learnteachcode.seoul.attendance.tracker.api.member.Member;
import org.learnteachcode.seoul.attendance.tracker.api.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Component
public class SpringDataJpaUserDetailsService implements UserDetailsService {

    private final MemberRepository repository;

    @Autowired
    public SpringDataJpaUserDetailsService(MemberRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Member member = this.repository.findByUsername(name);
        return new User(member.getUsername(), member.getPassword(),
                AuthorityUtils.createAuthorityList(member.getRoles()));
    }

}
// end::code[]

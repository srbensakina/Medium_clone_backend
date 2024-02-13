package com.api.medium_clone.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ArticleController.class)
@ComponentScan("com.api.medium_clone")
public class ArticleControllerTest {

   /* @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private UserService userService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private JWTGenerator jwtGenerator;

    private String token;

    @BeforeEach
    void setUp() {
        token = jwtGenerator.generateToken(new UsernamePasswordAuthenticationToken("test-user", "P4ssword"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldCreateArticleSuccessfully() throws Exception {
        when(userService.getUserByUsername("testuser")).thenReturn(new UserEntity());

        ArticleListResponseItemDto responseDto = new ArticleListResponseItemDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Article");
        when(articleService.createArticle(any(ArticleCreateRequestDto.class), any(UserEntity.class)))
                .thenReturn(responseDto);

        ArticleCreateRequestDto requestDto = new ArticleCreateRequestDto();
        requestDto.setTitle("Test Article");
        requestDto.setDescription("This is a test article");
        requestDto.setBody("Lorem ipsum dolor sit amet");

        mockMvc.perform(post("/api/articles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Article"));
    }*/
}
